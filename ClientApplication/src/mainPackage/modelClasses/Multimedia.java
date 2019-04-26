package mainPackage.modelClasses;

public class Multimedia extends Gadget<Boolean> {

    public Multimedia (String name, boolean state, int consumption, String room){
        super(name, state, consumption, room);
    }
    public Multimedia (String name, int consumption, String room){
        super(name, consumption, room);
        state = false;
    }
}
