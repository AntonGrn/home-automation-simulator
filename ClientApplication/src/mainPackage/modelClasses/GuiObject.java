package mainPackage.modelClasses;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GuiObject {

    private ImageView typeOfGadget; //ex Lampfalse
    private String gadgetName; //ex Lamp One
    private ImageView stateOfGadget; //ex switchButtonOff
    private int id; //for database usage


    public GuiObject(String typeOfGadget,String gadgetName,String stateOfGadget, int id) {
        this.typeOfGadget = new ImageView(new Image("/mainPackage/images/" + typeOfGadget + ".png"));
        this.gadgetName = gadgetName;
        this.stateOfGadget = new ImageView(new Image("/mainPackage/images/" + stateOfGadget + ".png"));
        this.id= id;

        this.typeOfGadget.setFitHeight(40);
        this.typeOfGadget.setFitWidth(40);

        this.stateOfGadget.setFitHeight(30);
        this.stateOfGadget.setFitWidth(70);
    }

    public GuiObject(String typeOfGadget){
        this.typeOfGadget = new ImageView(new Image("mainPackage/images/" + typeOfGadget + ".png"));

        this.typeOfGadget.setFitHeight(25);
        this.typeOfGadget.setFitWidth(60);
    }

    public String getGadgetName() {
        return gadgetName;
    }

    public ImageView getStateOfGadget() {
        return stateOfGadget;
    }

    public ImageView getTypeOfGadget() {
        return typeOfGadget;
    }

    public int getId() {
        return id;
    }
}
