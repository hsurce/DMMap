package Popups;

import Controllers.GlobalController;
import ItemSkeletons.ResourceYield;
import ItemSkeletons.Town;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class TownPopupController extends Popup {

    @FXML
    private ImageView TownPopupImageView;

    @FXML
    private AnchorPane TownPopupAnchorPane;

    @FXML
    private TextField TownPopupNameTextField;

    @FXML
    private TextArea TownPopupNotesTextArea;

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
            currentTown.setNotes(TownPopupNotesTextArea.getText());
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
        TownPopupNotesTextArea.setText("");
        TownPopupImageView.setImage(previewImage);
        if(town.getName() != null){
            TownPopupNameTextField.setText(town.getName());
        }
        if(town.getNotes() != null){
            TownPopupNotesTextArea.setText(town.getNotes());
        }

    }
}
