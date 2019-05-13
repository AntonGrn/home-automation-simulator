package mainPackage.modelClasses;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mainPackage.Main;


public abstract class Gadget<T> {

    private String name;
    protected T state;
    private int consumption;
    private String room;
    private final int id;

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
}
