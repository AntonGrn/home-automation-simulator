package mainPackage.modelClasses;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mainPackage.Main;


public abstract class Gadget<T> {

    private SimpleObjectProperty<javafx.scene.image.ImageView> typeImage;
    private String name;
    protected T state;
    private int consumption;
    private String room;
    private final int id;
    private SimpleObjectProperty<javafx.scene.image.ImageView> onOffImage;

    public Gadget(String name, T state, int consumption, String room, int id) { //constructor
        this.name = name;
        this.state = state;
        this.consumption = consumption;
        this.room = room;
        this.id = id;
    }

    public void setConsumption(int consumption) throws IllegalArgumentException {
        if (consumption < 0 || consumption > 3500) {
            throw new IllegalArgumentException("Consumption must be within 0-3500 W");
        } else {
            this.consumption = consumption;
        }
    }

    public int getConsumption() {
        return consumption;
    }

    public void setState(T state) {
        this.state = state;
    }

    public T getState() {
        return state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }

    public int getId() {
        return id;
    }

    //Used from tableview PropertyValue when loading gadgets.
    public SimpleObjectProperty<ImageView> typeImageProperty() {
        try {
            String type = this.getClass().getSimpleName();
            T stateOnOff = this.getState();
            System.out.println("Type: " + type);
            System.out.println("State: " + stateOnOff);
            System.out.println(type+stateOnOff+".png");

            typeImage = new SimpleObjectProperty<ImageView>(new ImageView(new Image("images/"+type + String.valueOf(stateOnOff) + ".png")));
            typeImage.get().setFitHeight(25);
            typeImage.get().setFitWidth(60);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("images 1");
            Main.getMainWindowController().exceptionLabel.setText("Could not load gadget images.. heh");
        }
        return typeImage;
    }

    //Used from tableview PropertyValue when loading gadgets.
    public SimpleObjectProperty<ImageView> onOffImageProperty() {
        try {
            if (this.state instanceof Boolean) {
                if (this.state.equals(true)) {
                    onOffImage = new SimpleObjectProperty<ImageView>(new ImageView(new Image(getClass().getResourceAsStream("src/mainPackage/images/switchButtonOn.png"))));
                } else {
                    onOffImage = new SimpleObjectProperty<ImageView>(new ImageView(new Image(getClass().getResourceAsStream("src/mainPackage/images/switchButtonOff.png"))));
                }
            } else {
                onOffImage = new SimpleObjectProperty<ImageView>(new ImageView(new Image(getClass().getResourceAsStream("src/mainPackage/images/heatButton.png"))));
            }
            onOffImage.get().setFitHeight(25);
            onOffImage.get().setFitWidth(60);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("images 2");
            Main.getMainWindowController().exceptionLabel.setText("could not load gadget images..");
        }
        return onOffImage;
    }
}
