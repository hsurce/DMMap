package ItemSkeletons;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.ArrayList;

/**
 * Created by Jakob on 6/11/2020.
 */
public class Town {
    private Image previewImage;
    private Point2D townCoordinate;
    private String notes;
    private String name;
    private ArrayList<Integer> raceDistribution;
    /**
     * Implementer også environments her!
     */
    public Town(Point2D townCoordinate, Image previewImage){
        this.townCoordinate = townCoordinate;
        this.previewImage = previewImage;
    }
    public Image getPreviewImage(){
        return previewImage;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getNotes(){
        return notes;
    }
    public void setNotes(String notes){
        this.notes = notes;
    }


    public Point2D getTownCoordinate() {
        return townCoordinate;
    }

    public void setTownCoordinate(Point2D townCoordinate) {
        this.townCoordinate = townCoordinate;
    }
}
