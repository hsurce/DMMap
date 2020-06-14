package Popups;

import javafx.stage.Stage;

/**
 * Created by Jakob on 1/6/2019.
 */
public abstract class Popup {
    Stage stage;

    public void closeStage(){
        stage.close();
    }

    public String getStageTitle(){
        return stage.getTitle();
    }
    public void show(){
        stage.show();
    }
}
