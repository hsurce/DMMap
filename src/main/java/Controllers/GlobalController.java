package Controllers;

import ItemSkeletons.Town;
import Popups.TownPopupController;

import java.util.ArrayList;

/**
 * Created by Jakob on 6/11/2020.
 */
public class GlobalController {
    TownPopupController townPopupController;
    ArrayList<Town> towns;

    public GlobalController(TownPopupController townPopupController){
        this.townPopupController = townPopupController;
        towns = new ArrayList<>();
    }

    public TownPopupController getTownPopupController() {
        return townPopupController;
    }

    public void addTown(Town town){
        towns.add(town);
    }
    public ArrayList<Town> getTowns(){
        return towns;
    }
}
