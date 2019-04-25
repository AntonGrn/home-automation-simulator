package mainPackage.modelClasses;

public class Door extends Gadget <Boolean> {

    public Door (String name, boolean state, int consumption, String room){
        super(name, state, consumption, room);
    }
    public Door (String name, int consumption, String room){
        super(name, consumption, room);
        state = false;
    }
}
