package mainPackage.modelClasses;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mainPackage.Main;


public abstract class Gadget<T> {

    private SimpleObjectProperty<javafx.scene.image.ImageView> typeImage = new SimpleObjectProperty<ImageView>();
    private String name;
    protected T state;
    private int consumption;
    private String room;
    private SimpleObjectProperty<javafx.scene.image.ImageView> onOffImage = new SimpleObjectProperty<ImageView>();

    public Gadget(String name, T state, int consumption, String room) { //Overloaded constructor for when objects are loaded from Server
        this.name = name;
        this.state = state;
        this.consumption = consumption;
        this.room = room;
    }

    public Gadget(String name, int consumption, String room) { //Overloaded constructor for when objects are created.
        this.name = name;
        this.consumption = consumption;
        this.room = room;
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

    public ImageView typeImageProperty() {
        try {
            String type = this.getClass().getSimpleName();
            T stateOnOff = this.getState();

            typeImage = new SimpleObjectProperty<ImageView>(new ImageView(new Image(String.valueOf(type + String.valueOf(stateOnOff) + ".png"))));
            typeImage.get().setFitHeight(25);
            typeImage.get().setFitWidth(60);
        } catch (Exception e) {
            e.printStackTrace();
            Main.getMainWindowController().exceptionLabel.setText("Could not load gadget images..");
        }
        return typeImage.get();
    }

    public ImageView onOffImageProperty() {
        try {
            if (this.state instanceof Boolean) {
                if (this.state.equals(true)) {
                    onOffImage = new SimpleObjectProperty<ImageView>(new ImageView(new Image(getClass().getResourceAsStream("/src/mainPackage/images/switchButtonOn.png"))));
                } else {
                    onOffImage = new SimpleObjectProperty<ImageView>(new ImageView(new Image(getClass().getResourceAsStream("/src/mainPackage/images/switchButtonOff.png"))));
                }
            } else {
                onOffImage = new SimpleObjectProperty<ImageView>(new ImageView(new Image(getClass().getResourceAsStream("/src/mainPackage/images/heatButton.png"))));
            }
            onOffImage.get().setFitWidth(60);
            onOffImage.get().setFitHeight(25);
        } catch (Exception e) {
            e.printStackTrace();
            Main.getMainWindowController().exceptionLabel.setText("could not load gadget images..");
        }
        return onOffImage.get();
    }
}
