package mainPackage.dynamicFrames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import mainPackage.Main;
import mainPackage.ServerConnection;
import mainPackage.modelClasses.Account;
import mainPackage.AccountLoggedin;
import mainPackage.DynamicFrame;

public class TestController implements DynamicFrame {


    @FXML
    public void initialize() {

        Main.getMainWindowController().setCurrentDynamicFrameController(this);

    }

    @FXML

    public void logout(ActionEvent event) {
        try {
            Main.getMainWindowController().requestsToServer.put("16");
            Thread.sleep(100);
            ServerConnection.getInstance().closeResources();
        } catch (InterruptedException e) {
            System.out.println("Exit interrupted");
        }
    }

    public void updateFrame() {
    }
}
