package mainPackage.modelClasses;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GuiObject {

    private ImageView typeOfGadget; //ex LampFalse.png
    private String gadgetName; //ex Lamp One
    private ImageView stateOfGadget; //ex switchButtonOff.png


    public GuiObject(String typeOfGadget,String gadgetName,String stateOfGadget) {
        this.typeOfGadget = new ImageView(new Image("mainPackage/images/" + typeOfGadget + ".png"));
        this.gadgetName = gadgetName;
        this.stateOfGadget = new ImageView(new Image("mainPackage/images/" + stateOfGadget + ".png"));

        this.typeOfGadget.setFitHeight(25);
        this.typeOfGadget.setFitWidth(60);

        this.stateOfGadget.setFitHeight(25);
        this.stateOfGadget.setFitWidth(60);
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
}
