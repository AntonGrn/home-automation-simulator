package mainPackage.modelClasses;

public class Heat extends Gadget<Integer> {

    public Heat(String name, int state, int consumption, String room) {
        super(name, state, consumption, room);
    }

    public Heat(String name, int consumption, String room) {
        super(name, consumption, room);
        state = 0;
    }

}
