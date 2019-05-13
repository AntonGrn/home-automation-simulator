package mainPackage.dynamicFrames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import mainPackage.Main;
import mainPackage.ServerConnection;
import mainPackage.modelClasses.Account;
import mainPackage.AccountLoggedin;
import mainPackage.DynamicFrame;
import mainPackage.modelClasses.Gadget;
import mainPackage.modelClasses.Lamp;

import java.security.SecureRandom;

public class TestController implements DynamicFrame {


    @FXML
    public void initialize() {

        Main.getMainWindowController().setCurrentDynamicFrameController(this);

    }

    @FXML
    public void logout (ActionEvent event) {
            try {
                Main.getMainWindowController().requestsToServer.put("16");
                Thread.sleep(100);
                ServerConnection.getInstance().closeResources();
            }catch (InterruptedException e) {
                System.out.println("Exit interrupted");
            }
    }

    @FXML
    public void toggleLamp (ActionEvent event) {
        for(Gadget gadget : Main.getMainWindowController().gadgetList) {
            if(gadget instanceof Lamp) {
                boolean toggleState = !(Boolean)gadget.getState();
                try {
                    Main.getMainWindowController().requestsToServer.put(
                            "3:" + String.valueOf(gadget.getId()) +":" + (toggleState ? "1":"0") );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @FXML
    public void addGadget (ActionEvent event) {
        SecureRandom sRandom = new SecureRandom();
        int randomInt = sRandom.nextInt(100);
        String type = "Lamp";
        String name = String.format("%s%s", "Lamp", randomInt);
        String room = "Kitchen";
        String consumption = "25";
        try {
            Main.getMainWindowController().requestsToServer.put(
                    "7:" + type +":" + name + ":" + room + ":" + consumption);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //test for changing rooms
    @FXML
    public void setChosenRoomKitchen(ActionEvent event) {
        Main.getMainWindowController().chosenRoom.setValue("kitchen");
    }

    @FXML
    public void setChosenRoomLivingroom(ActionEvent event) {
        Main.getMainWindowController().chosenRoom.setValue("livingroom");
    }

    public void updateFrame() {
        System.out.println("UpdateFrame");
        for(Gadget g : Main.getMainWindowController().gadgetList) {
            System.out.println("Gadget state =  " + g.getState());
        }
    }
}
