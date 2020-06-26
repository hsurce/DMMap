package Controllers;

import Handlers.FileHandler;
import ItemSkeletons.Point2DSerializable;
import ItemSkeletons.Town;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Optional;

import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;

public class StartController {

    @FXML
    private VBox StartVBox;

    @FXML
    private MenuBar StartMenuBar;

    @FXML
    private Menu StartMenuFile;

    @FXML
    private MenuItem MenuNew;

    @FXML
    private MenuItem MenuOpen;

    @FXML
    private MenuItem MenuSave;

    @FXML
    private MenuItem MenuQuit;

    @FXML
    private ToggleButton StartLayoutToggleSet;

    @FXML
    private ToggleButton StartLayoutToggleLookup;

    @FXML
    private Label StartLayoutTempLabel;

    @FXML
    private StackPane StartLayoutStackPane;

    @FXML
    private ImageView StartLayoutImageView;

    @FXML
    private TextField townSearchBar;

    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
    private static final int MIN_PIXELS = 10;
    Stage curStage;
    double imageWidth;
    double imageHeight;
    private boolean setCoordinate = false;
    private boolean readTownNearCursor = false;
    private ArrayList<Town> townList;
    private GlobalController globalController;
    private ArrayList<String> mapChoices = new ArrayList<>();
     private FileHandler fileHandler;

    public void initialize(Stage stage, GlobalController globalController) {
        curStage = stage;
        this.globalController = globalController;
        fileHandler = new FileHandler(globalController);
        initiateOnMenuClicked();
        initiateToggleButtons();
        giveReferencesToGlobalController();
    }

    private void giveReferencesToGlobalController() {
        globalController.setTownsSearchBar(townSearchBar);
        globalController.setStartLayoutImageView(StartLayoutImageView);
    }


    private void initiateToggleButtons() {
        StartLayoutToggleLookup.setOnAction(e -> {
            if (StartLayoutToggleLookup.isSelected()) {
                readTownNearCursor = true;
                StartLayoutToggleSet.setSelected(false);
                setCoordinate = false;
            } else readTownNearCursor = false;
        });
        StartLayoutToggleSet.setOnAction(e -> {
            if (StartLayoutToggleSet.isSelected()) {
                setCoordinate = true;
                StartLayoutToggleLookup.setSelected(false);
                readTownNearCursor = false;
            } else setCoordinate = false;
        });
    }

    private void initiateZoomAndPanImageView() {
        StartLayoutImageView.setPreserveRatio(true);
        reset(StartLayoutImageView, imageWidth / 2, imageHeight / 2);

        ObjectProperty<Point2D> mouseDown = new SimpleObjectProperty<>();

        StartLayoutImageView.setOnMousePressed(e -> {
            Point2D mousePress = imageViewToImage(StartLayoutImageView, new Point2D(e.getX(), e.getY()));
            mouseDown.set(mousePress);
        });

        StartLayoutImageView.setOnMouseDragged(e -> {
            Point2D dragPoint = imageViewToImage(StartLayoutImageView, new Point2D(e.getX(), e.getY()));
            shift(StartLayoutImageView, dragPoint.subtract(mouseDown.get()));
            mouseDown.set(imageViewToImage(StartLayoutImageView, new Point2D(e.getX(), e.getY())));
        });

        StartLayoutImageView.setOnScroll(e -> {
            double delta = e.getDeltaY();
            Rectangle2D viewport = StartLayoutImageView.getViewport();

            double scale = clamp(Math.pow(1.005, delta),  // altered the value from 1.01to zoom slower
                    // don't scale so we're zoomed in to fewer than MIN_PIXELS in any direction:
                    Math.min(MIN_PIXELS / viewport.getWidth(), MIN_PIXELS / viewport.getHeight()),
                    // don't scale so that we're bigger than image dimensions:
                    Math.max(imageWidth / viewport.getWidth(), imageHeight / viewport.getHeight())
            );
            if (scale != 1.0) {
                Point2D mouse = imageViewToImage(StartLayoutImageView, new Point2D(e.getX(), e.getY()));
                double newWidth = viewport.getWidth();
                double newHeight = viewport.getHeight();
                double imageViewRatio = (StartLayoutImageView.getFitWidth() / StartLayoutImageView.getFitHeight());
                double viewportRatio = (newWidth / newHeight);
                if (viewportRatio < imageViewRatio) {
                    // adjust width to be proportional with height
                    newHeight = newHeight * scale;
                    newWidth = newHeight * imageViewRatio;
                    if (newWidth > StartLayoutImageView.getImage().getWidth()) {
                        newWidth = StartLayoutImageView.getImage().getWidth();
                    }
                } else {
                    // adjust height to be proportional with width
                    newWidth = newWidth * scale;
                    newHeight = newWidth / imageViewRatio;
                    if (newHeight > StartLayoutImageView.getImage().getHeight()) {
                        newHeight = StartLayoutImageView.getImage().getHeight();
                    }
                }

                // To keep the visual point under the mouse from moving, we need
                // (x - newViewportMinX) / (x - currentViewportMinX) = scale
                // where x is the mouse X coordinate in the image
                // solving this for newViewportMinX gives
                // newViewportMinX = x - (x - currentViewportMinX) * scale
                // we then clamp this value so the image never scrolls out
                // of the imageview:
                double newMinX = 0;
                if (newWidth < StartLayoutImageView.getImage().getWidth()) {
                    newMinX = clamp(mouse.getX() - (mouse.getX() - viewport.getMinX()) * scale,
                            0, imageWidth - newWidth);
                }
                double newMinY = 0;
                if (newHeight < StartLayoutImageView.getImage().getHeight()) {
                    newMinY = clamp(mouse.getY() - (mouse.getY() - viewport.getMinY()) * scale,
                            0, imageHeight - newHeight);
                }

                StartLayoutImageView.setViewport(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
            }
        });


        //StartLayoutImageView.fitWidthProperty().bind(StartLayoutAnchorPane.widthProperty());
        //StartLayoutImageView.fitHeightProperty().bind(StartLayoutAnchorPane.heightProperty());

    }


    // reset to the top left:
    private void reset(ImageView imageView, double width, double height) {
        imageView.setViewport(new Rectangle2D(0, 0, width, height));
    }

    // shift the viewport of the imageView by the specified delta, clamping so
    // the viewport does not move off the actual image:
    private void shift(ImageView imageView, Point2D delta) {
        Rectangle2D viewport = imageView.getViewport();
        double width = imageView.getImage().getWidth();
        double height = imageView.getImage().getHeight();
        double maxX = width - viewport.getWidth();
        double maxY = height - viewport.getHeight();
        double minX = clamp(viewport.getMinX() - delta.getX(), 0, maxX);
        double minY = clamp(viewport.getMinY() - delta.getY(), 0, maxY);
        if (minX < 0.0) {
            minX = 0.0;
        }
        if (minY < 0.0) {
            minY = 0.0;
        }
        imageView.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));
    }

    private double clamp(double value, double min, double max) {

        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    // convert mouse coordinates in the imageView to coordinates in the actual image:
    private Point2D imageViewToImage(ImageView imageView, Point2D imageViewCoordinates) {
        double xProportion = imageViewCoordinates.getX() / imageView.getBoundsInLocal().getWidth();
        double yProportion = imageViewCoordinates.getY() / imageView.getBoundsInLocal().getHeight();

        Rectangle2D viewport = imageView.getViewport();
        return new Point2D(
                viewport.getMinX() + xProportion * viewport.getWidth(),
                viewport.getMinY() + yProportion * viewport.getHeight());
    }


    public void initiateOnMenuClicked() {
        MenuNew.setOnAction(e -> MenuNewAction());
        MenuOpen.setOnAction(e -> MenuOpenAction());
    }

    private void MenuOpenAction() {
        /**
         * Popup vindue med en choice box der har alle de forskellige directories i sig.
         */
        mapChoices = new ArrayList<>();
        fileHandler.populateMapChoiceList(mapChoices);
        ChoiceDialog<String> dialog = new ChoiceDialog<>(mapChoices.get(0), mapChoices);
        dialog.setTitle("Choose a map project");
        dialog.setHeaderText("Here you can choose one of your map projects!");
        dialog.setContentText("Choose your map project:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(e -> {
            File[] directoryItems = fileHandler.loadMapProjectDir(result.get());
            if(directoryItems != null){
                for(File directoryItem : directoryItems){
                    if(directoryItem.getName().contains("image")){
                        globalController.setCurrentDestName(result.get());
                        Image image = fileHandler.loadFile(directoryItem);
                        handleImage(image, directoryItem, false);
                    }
                    if(directoryItem.getName().equals("towns.bin")){
                        try {
                            fileHandler.loadTowns(directoryItem);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        System.out.println("HEJ!");
                    }
                }
            }
            else{
                fileHandler.directoryErrorAlert();
            }

        });
    }


    private void MenuNewAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        File file = fileChooser.showOpenDialog(curStage);
        if (file != null) {
            Image image = fileHandler.loadFile(file);
            handleImage(image, file, true);
        }

    }

    private void handleImage(Image image, File file, boolean isNewProject) {
        imageHeight = image.getHeight();
        imageWidth = image.getWidth();
        StartLayoutImageView.setImage(image);
        initiateZoomAndPanImageView();
        initiateCreateOrLookupCoordinate();
        StartLayoutTempLabel.setVisible(false);
        if(isNewProject) {
            fileHandler.saveImageToDest(file, makePopupDialog());
        }
    }

    /**
    private void loadFile(File file, boolean isNewProject) {
        try {

            Image image = new Image(file.toURI().toURL().toString(), false);
            imageHeight = image.getHeight();
            imageWidth = image.getWidth();
            StartLayoutImageView.setImage(image);
            initiateZoomAndPanImageView();
            initiateCreateOrLookupCoordinate();
            StartLayoutTempLabel.setVisible(false);
            if(isNewProject) {
                fileHandler.saveImageToDest(file, makePopupDialog());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }
 */

    private String makePopupDialog() {
        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Name Dialog");
        dialog.setHeaderText("We're going to save your image as a project!");
        dialog.setContentText("Please enter your map name:");
        Optional<String> result = dialog.showAndWait();
        final String[] returnResult = new String[1];
        result.ifPresent(name -> returnResult[0] = name.toString());
        globalController.setCurrentDestName(returnResult[0]);
        if(result.isPresent()){
            globalController.setTowns(new ArrayList<>());
        }
        return returnResult[0];
    }

    private void initiateCreateOrLookupCoordinate() {
        StartLayoutImageView.setOnMouseClicked(e -> {
            Point2D destination = imageViewToImage(StartLayoutImageView, new Point2D(e.getX(), e.getY()));
            if (e.getClickCount() == 2 && setCoordinate) {

                Town town = new Town(new Point2DSerializable(destination));
                globalController.getTownPopupController().createPopup(town, globalController.createPreviewImage(destination));
                globalController.getTownPopupController().show();

            }
            if (e.getClickCount() == 2 && readTownNearCursor) {
                if (globalController.getTowns() != null) {
                    double shortestDistance = -1;
                    Town closestTown = null;
                    for (Town town : globalController.getTowns()) {
                        if (destination.distance(town.getTownCoordinate().getPoint()) < shortestDistance || shortestDistance == -1) {
                            shortestDistance = destination.distance(town.getTownCoordinate().getPoint());
                            closestTown = town;
                        }
                    }
                    shift(StartLayoutImageView, destination.subtract(closestTown.getTownCoordinate().getPoint()));
                    globalController.getTownPopupController().createPopup(closestTown, globalController.createPreviewImage(closestTown.getTownCoordinate().getPoint()));
                    globalController.getTownPopupController().show();
                }
            }
        });
    }






}
