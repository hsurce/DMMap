package Controllers;

import Popups.TownPopupController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Jakob on 5/10/2020.
 */
public class main extends Application {
    private StartController startController;
    private double x,y;
    private double positionX = 0;
    private double positionY = 0;
    private GlobalController globalController;
    private TownPopupController townPopupController;
    public static void main(String[] args){
    launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(accessFXMLFile("StartLayout.fxml"));
        Parent startroot = fxmlLoader.load();
        startController = fxmlLoader.getController();
        loadFXMLFiles();

        globalController = new GlobalController(townPopupController);
        primaryStage.setScene(new Scene(startroot));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        startController.initialize(primaryStage, globalController);
        townPopupController.initialize(globalController);
    }

    private void loadFXMLFiles() throws IOException {
        FXMLLoader townPopupLoader = new FXMLLoader(accessFXMLFile("TownPopupLayout.fxml"));
        Parent townpopuproot = townPopupLoader.load();
        townPopupController = (TownPopupController)townPopupLoader.getController();
    }

    /**
     *
     * @param s being the name of the file needed to be found in resources using the proper format ending (i.e. "StartLayout.fxml")
     * @return URL format for FXMLLoader
     */
    public static URL accessFXMLFile(String s) {
        String resource = s;

        // this is the path within the jar file
        URL input = Thread.currentThread().getContextClassLoader().getResource("XMLFiles/"+resource);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = main.class.getClass().getResource("../XMLFiles/"+resource);
        }

        return input;
    }
}
