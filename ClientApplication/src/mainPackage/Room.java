package mainPackage;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Room {
    private StringProperty name;

    public Room(String name) {
        this.name = new SimpleStringProperty(name);
    }
    public void setName(StringProperty name){
        this.name = name;
    }
    public String getName(){
        return name.get();
    }
}
