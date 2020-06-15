package Controllers;

import ItemSkeletons.Town;
import Popups.TownPopupController;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Jakob on 6/11/2020.
 */
public class GlobalController {
    private TownPopupController townPopupController;
    private ArrayList<Town> towns;
    private ArrayList<String> townsNameList;
    private String currentDestName;
    private AutoCompletionBinding<String> autoCompletionBindingTown;
    private TextField townsSearchBar;
    private ImageView startLayoutImageView;

    public GlobalController(TownPopupController townPopupController){
        this.townPopupController = townPopupController;
        towns = new ArrayList<>();
        townsNameList = new ArrayList<>();
    }

    public TownPopupController getTownPopupController() {
        return townPopupController;
    }

    public void addTown(Town town){
        towns.add(town);
        updateTownNamesList(town);
    }
    public ArrayList<Town> getTowns(){
        return towns;
    }

    public void saveTownsToBin() {
        try{
            String tmpPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            File tmpFile = new File(tmpPath);
            tmpFile = tmpFile.getParentFile();
            String file = tmpFile.getAbsolutePath() + "/Directories/" + currentDestName + "/";
            File absFile = new File(file);

            FileOutputStream fos = new FileOutputStream(absFile.getPath()+"/towns.bin");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(towns);
            oos.close();
            fos.close();
        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }

    public void setCurrentDestName(String currentDestName) {
        this.currentDestName = currentDestName;
    }

    public void setTowns(ArrayList<Town> towns) {
        this.towns = towns;
        updateTownNamesList();
    }
    public Town getTown(String s) {
        for (Town town : towns) {
            if (town.getName().equals(s)) {
                return town;
            }

        }
        return null;
    }

    public void updateTownNamesList(){
        townsNameList = new ArrayList<>();
        for(Town town: towns){
            townsNameList.add(town.getName());
        }
        bindAutoCompleteTown(townsNameList);
    }
    public void updateTownNamesList(Town town){
        townsNameList.add(town.getName());
        bindAutoCompleteTown(townsNameList);
    }

    public ArrayList<String> getTownsNameList(){
        return townsNameList;
    }

    public void setTownsSearchBar(TextField townsSearchBar){
        this.townsSearchBar = townsSearchBar;
    }
    public void setStartLayoutImageView(ImageView imageView){
        startLayoutImageView = imageView;
    }

    public void initializeTownSearchBar(javafx.scene.control.TextField townSearchBar) {
        TextFields.bindAutoCompletion(townSearchBar,getTownsNameList());
        bindAutoCompleteTown(getTownsNameList());
    }

    private void bindAutoCompleteTown(ArrayList<String> townNames){
        if(autoCompletionBindingTown != null) {
            autoCompletionBindingTown.dispose();
        }
        autoCompletionBindingTown = TextFields.bindAutoCompletion(townsSearchBar, townNames);
        autoCompletionBindingTown.setOnAutoCompleted(event -> {
            getTownPopupController().createPopup(getTown(event.getCompletion()), createPreviewImage(getTown(event.getCompletion()).getTownCoordinate().getPoint()));
            getTownPopupController().show();
        });

    }

    public javafx.scene.image.Image createPreviewImage(Point2D destination) {
        PixelReader reader = startLayoutImageView.getImage().getPixelReader();
        int previewWidthHeight = 500;
        int imageXCoord = (int) destination.getX() - previewWidthHeight / 2;
        int imageYCoord = (int) destination.getY() - previewWidthHeight / 2;
        if (imageXCoord < 0) imageXCoord = 0;
        if (imageYCoord < 0) imageYCoord = 0;
        WritableImage previewImage = new WritableImage(reader, imageXCoord, imageYCoord, previewWidthHeight, previewWidthHeight);
        return previewImage;
    }
}
