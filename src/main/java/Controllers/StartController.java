package Controllers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

import java.io.File;
import java.net.MalformedURLException;

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
    private MenuItem MenuClose;

    @FXML
    private MenuItem MenuSave;

    @FXML
    private MenuItem MenuSaveAs;

    @FXML
    private MenuItem MenuQuit;

    @FXML
    private Menu StartMenuHelp;

    @FXML
    private Label StartLayoutTempLabel;

    @FXML
    private StackPane StartLayoutStackPane;

    @FXML
    private ImageView StartLayoutImageView;
    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
    private static final int MIN_PIXELS = 10;
    Stage curStage;
    double imageWidth;
    double imageHeight;

    public void initialize(Stage stage){
        curStage = stage;
        initiateOnMenuClicked();


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


    public void initiateOnMenuClicked(){
        MenuOpen.setOnAction(e -> MenuOpenAction());
    }

    private void MenuOpenAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        File file = fileChooser.showOpenDialog(curStage);
        if (file != null){
            openFile(file);
        }

    }

    private void openFile(File file) {
        try {
            Image image = new Image(file.toURI().toURL().toString(), false);
            imageHeight = image.getHeight();
            imageWidth = image.getWidth();
            StartLayoutImageView.setImage(image);
            initiateZoomAndPanImageView();
            initiateCreateButton();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }



    }

    private void initiateCreateButton() {
        StartLayoutImageView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Point2D destination = imageViewToImage(StartLayoutImageView, new Point2D(e.getX(), e.getY()));
                System.out.println(destination.getX());
                Button b = createButton(destination.getX(), destination.getY());
                StartLayoutStackPane.getChildren().add(b);
                b.setVisible(true);
            }
        });
    }

    private Button createButton(double x, double y) {
        final Button killButton = new Button("Kill the evil witch");
        killButton.setStyle("-fx-base: firebrick;");
        killButton.setTranslateX(x);
        killButton.setTranslateY(y);
        killButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                killButton.setStyle("-fx-base: forestgreen;");
                killButton.setText("Ding-Dong! The Witch is Dead");
            }
        });
        return killButton;
    }
}
