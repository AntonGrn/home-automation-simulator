package mainPackage.modelClasses;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;

public class RoomSlider {
    private static RoomSlider myObject = null; //Singleton object

    private SimpleObjectProperty<javafx.scene.control.Button> bedRoom;
    private SimpleObjectProperty<javafx.scene.control.Button> kitchen;
    private SimpleObjectProperty<javafx.scene.control.Button> garage;
    private SimpleObjectProperty<javafx.scene.control.Button> toilet;
    private SimpleObjectProperty<javafx.scene.control.Button> livingRoom;

    private RoomSlider() {
        this.bedRoom = new SimpleObjectProperty<>(new Button("Bedroom"));
        this.kitchen = new SimpleObjectProperty<>(new Button("Kitchen"));
        this.garage = new SimpleObjectProperty<>(new Button("Garage"));
        this.toilet = new SimpleObjectProperty<>(new Button("Toilet"));
        this.livingRoom = new SimpleObjectProperty<>(new Button("Living Room"));

        //use this as reference when inserting gadgets in different rooms in roomList
        this.bedRoom.get().setUserData("Bedroom");
        this.kitchen.get().setUserData("Kitchen");
        this.garage.get().setUserData("Garage");
        this.toilet.get().setUserData("Toilet");
        this.livingRoom.get().setUserData("Livingroom");

        //set size
        this.bedRoom.get().setPrefSize(100, 60);
        this.kitchen.get().setPrefSize(100, 60);
        this.garage.get().setPrefSize(100, 60);
        this.toilet.get().setPrefSize(100, 60);
        this.livingRoom.get().setPrefSize(200, 60);
    }

    public static RoomSlider getRoomSliderInstance() {
        if (myObject == null) {
            myObject = new RoomSlider();
        }
        return myObject;
    }

    public Button getBedRoom() {
        return bedRoom.get();
    }

    public Button getKitchen() {
        return kitchen.get();
    }

    public Button getGarage() {
        return garage.get();
    }

    public Button getToilet() {
        return toilet.get();
    }

    public Button getLivingRoom() {
        return livingRoom.get();
    }
}
