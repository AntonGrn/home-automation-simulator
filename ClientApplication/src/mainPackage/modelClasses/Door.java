package mainPackage.modelClasses;

public class Door extends Gadget <Boolean> {

    public Door(String name, Boolean state, int consumption, String room, int id) {
        super(name, state, consumption, room, id);
    }
}
