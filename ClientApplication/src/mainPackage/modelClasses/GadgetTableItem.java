package mainPackage.modelClasses;

public class GadgetTableItem {

    private String name;
    private String type;
    private String room;
    private int  id;

    public GadgetTableItem(String name, String type, String room, int id) {
        this.name = name;
        this.type = type;
        this.room = room;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getRoom() {
        return room;
    }

    public int getId() {
        return id;
    }
}
