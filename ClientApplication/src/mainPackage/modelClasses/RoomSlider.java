package mainPackage.modelClasses;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class RoomSlider {

    private SimpleObjectProperty<javafx.scene.control.Button> bedRoom;
    private SimpleObjectProperty<javafx.scene.control.Button> kitchen;
    private SimpleObjectProperty<javafx.scene.control.Button> garage;
    private SimpleObjectProperty<javafx.scene.control.Button> toilet;
    private SimpleObjectProperty<javafx.scene.control.Button> livingRoom;

    public RoomSlider() {
        this.bedRoom = new SimpleObjectProperty<>(new Button("Bedroom"));
        this.kitchen = new SimpleObjectProperty<>(new Button("Kitchen"));
        this.garage = new SimpleObjectProperty<>(new Button("Garage"));
        this.toilet = new SimpleObjectProperty<>(new Button("Toilet"));
        this.livingRoom = new SimpleObjectProperty<>(new Button("Living Room"));

        this.bedRoom.get().setPrefSize(100,60);
        this.kitchen.get().setPrefSize(100,60);
        this.garage.get().setPrefSize(100,60);
        this.toilet.get().setPrefSize(100,60);
        this.livingRoom.get().setPrefSize(100,60);

        /*Needs to set an new Scene for the dynamicFrame in Rooms.fxml (same principle as mainWindow)*/

        this.bedRoom.get().setOnAction(e->{
        /*create a method that takes the list "roomList" from MainWindowController and
        iterates through it and fills a tableview with all the information about all gadgets.

        this needs to be done to all instance variables..
         */
        });


        /*same as above..*/
        this.kitchen.get().setOnAction(e->{});
        this.garage.get().setOnAction(e->{});
        this.toilet.get().setOnAction(e->{});
        this.livingRoom.get().setOnAction(e->{});
    }
}
