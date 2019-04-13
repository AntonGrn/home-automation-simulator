package mainPackage;

public class Lamp extends Gadget<Boolean>{

    public Lamp (String name, boolean state, int consumption, String room) {
        super(name, state, consumption, room);
    }

    public Lamp (String name, int consumption, String room) {
        super(name, consumption, room);
        state = false;
    }

}
