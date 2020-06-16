package Handlers;

import Controllers.GlobalController;
import ItemSkeletons.Town;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * Created by Jakob on 6/15/2020.
 */
public class FileHandler {
    private final String dirPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    private GlobalController globalController;

    public FileHandler(GlobalController globalController){
        this.globalController = globalController;
    }
    public void saveImageToDest(File imgSrc, String dest) {
        try {
            File tmpFile = new File(dirPath);
            tmpFile = tmpFile.getParentFile();
            String file = tmpFile.getAbsolutePath() + "/Directories/";
            File newDir = new File(file + dest);
            newDir.mkdir();
            String[] fileFormat = imgSrc.getPath().split("\\.");
            System.out.println(fileFormat);
            File absFile = new File(file + dest + "/image." + fileFormat[1]);
            Files.copy(imgSrc.toPath(), absFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }


    }

    public File[] loadMapProjectDir(String mapProjectDir) {
        File tmpfile = new File(dirPath).getParentFile();
        File dir = new File(tmpfile.getAbsolutePath()+ "/Directories/" + mapProjectDir);
        File[] directoryItems = dir.listFiles();
        return directoryItems;
    }

    public void loadTowns(File townsBin) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois;
        fis = new FileInputStream(townsBin);
        ois = new ObjectInputStream(fis);
        try{
            globalController.setTowns((ArrayList<Town>) ois.readObject());
            globalController.initializeTownSearchBar();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Image loadFile(File file) {
        Image image = null;
        try {

            image = new Image(file.toURI().toURL().toString(), false);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return image;
    }


    public void populateMapChoiceList(ArrayList<String> mapChoices) {
        File tmpfile = new File(dirPath).getParentFile();
        File dir = new File(tmpfile.getAbsolutePath()+ "/Directories/");
        System.out.println(dir.getPath());
        File[] dirListing = dir.listFiles();
        if(dirListing != null) {
            for (File file : dirListing) {
                mapChoices.add(file.getName());
                System.out.println(file.getName());
            }
        }
        else{
            directoryErrorAlert();

        }
    }

    public void directoryErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Missing directories error");
        alert.setHeaderText("No directories found!");
        alert.setContentText("There was an error with finding any directory or finding files in the chosen directory!");

        alert.showAndWait();
    }


}
