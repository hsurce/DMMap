package ItemSkeletons;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jakob on 6/11/2020.
 */
public class Town implements Serializable{
    private Point2DSerializable townCoordinate;
    private String notes;
    private String name;
    private ArrayList<Integer> raceDistribution;
    /**
     * Implementer også environments her!
     */
    public Town(Point2DSerializable townCoordinate){
        this.townCoordinate = townCoordinate;
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


    public Point2DSerializable getTownCoordinate() {
        return townCoordinate;
    }

    public void setTownCoordinate(Point2DSerializable townCoordinate) {
        this.townCoordinate = townCoordinate;
    }
}
