package mainPackage.dynamicFrames;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;;
import mainPackage.Main;
import mainPackage.ServerConnection;
import mainPackage.DynamicFrame;
import mainPackage.modelClasses.Gadget;
import mainPackage.modelClasses.Lamp;

import java.security.SecureRandom;

public class TestController implements DynamicFrame {

    @FXML
    public Label label1, label2, label3;


    @FXML
    public void initialize() {
        Main.getMainWindowController().setCurrentDynamicFrameController(this);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateFrame();
            }
        });
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

    public void updateFrame() {
        label1.setText(Main.getMainWindowController().gadgetList.get(0).getName() + " = " + Main.getMainWindowController().gadgetList.get(0).getState());
        label2.setText(Main.getMainWindowController().gadgetList.get(1).getName() + "  = " + Main.getMainWindowController().gadgetList.get(1).getState());
        label3.setText(Main.getMainWindowController().gadgetList.get(2).getName() + "  = " + Main.getMainWindowController().gadgetList.get(2).getState());
    }
}
