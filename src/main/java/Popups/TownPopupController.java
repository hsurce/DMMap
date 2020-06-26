package Popups;

import Controllers.GlobalController;
import ItemSkeletons.ResourceYield;
import ItemSkeletons.Town;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;


public class TownPopupController extends Popup {

    @FXML
    private ImageView TownPopupImageView;

    @FXML
    private AnchorPane TownPopupAnchorPane;

    @FXML
    private TextField TownPopupNameTextField;

    @FXML
    private TextFlow TownPopupTextFlow;

    @FXML
    private Button TownPopupAddEnvironmentButton;

    @FXML
    private ChoiceBox<String> TownPopupTradeRouteChoiceBox;

    @FXML
    private ChoiceBox<Integer> TownPopupRelationshipChoiceBox;

    @FXML
    private ChoiceBox<String> TownPopupAddEnvironmentChoiceBox;

    @FXML
    private Button TownPopupAddTradeRouteButton;

    @FXML
    private Button TownPopupSaveButton;

    @FXML
    private Button TownPopupCloseButton;

    @FXML
    private TableView<ResourceYield> TownPopupResourceYieldTableView;

    private Town currentTown;
    private GlobalController globalController;

    /** TODO: 16-06-2020
     * lav en edit knap der sætter notes i "EDIT" tilstand. Den er i show tilstand fra start af.
     * Når byen loades eller show tilstand bliver togglet læser den noterne ind og manipulerer teksten efter behov
     * eksempelvis kan der stå IMG="C:/Path/Til/Billede" og der laves et imageview med det billede i.
     *
     *
     * Hav som udgangspunkt et ScrollPane og populate med labels / imageviews passende til det layout der er skrevet ind i edit text boksen.
     * Hvis EDIT bliver trykket, (eller population ikke fandt sted) skjul da ScrollPane og indhold og vis TextBox.
     */

    public void initialize(GlobalController globalController){
        this.globalController = globalController;
        super.stage = new Stage();
        super.stage.setTitle("Town editor");
        Scene scene = new Scene(TownPopupAnchorPane, 800, 600);
        super.stage.setScene(scene);
        initializeButtons();

    }

    private void initializeButtons() {
        TownPopupSaveButton.setOnAction(e -> {
            /**
             * Save funktionalitet
             */
            String concatNoteString = "";
            for(Node node: TownPopupTextFlow.getChildren()){
                if(node.getClass().equals(TextField.class)){
                    TextField textField = (TextField)node;
                    concatNoteString += textField.getText();
                }

            }
            currentTown.setNotes(concatNoteString);
            currentTown.setName(TownPopupNameTextField.getText());
            if(globalController.getTowns() != null) {
                if (!globalController.getTowns().contains(currentTown)) {
                    globalController.addTown(currentTown);
                }
            }
            globalController.saveTownsToBin();

        });
    }

    public void createPopup(Town town, Image previewImage) {
        currentTown = town;
        /**
         * * HUSK AT CLEAR AL INFORMATION NÅR DENNE FUNKTION BLIVER KALDT SÅ DER IKKE ER POTENTIELT OVERLAP MED FORRIG POPUP!
         */
        TownPopupNameTextField.setText("");
        TownPopupTextFlow.getChildren().removeAll();
        TownPopupImageView.setImage(previewImage);
        if(town.getName() != null){
            TownPopupNameTextField.setText(town.getName());
        }
        if(town.getNotes() != null){

            TownPopupTextFlow.getChildren().add(new Text(town.getNotes()));
        }

    }
}
