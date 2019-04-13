package mainPackage;

public abstract class Gadget<T> {

    private String name;
    protected T state;
    private int consumption;
    private String room;

    public Gadget(String name, T state, int consumption, String room) {
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
        if(consumption < 0 || consumption > 3500) {
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
    public T getState(){
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
}
