package mainPackage.dynamicFrames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import mainPackage.DynamicFrame;
import mainPackage.Main;
import mainPackage.ServerConnection;

public class SettingsController implements DynamicFrame {

    @FXML
    private Button btnLogOut;

    @FXML
    private TextArea textAreaInfoAboutUs;

    public void initialize(){
        //making sure so the mainWindow knows which controller that is in charge.
        Main.getMainWindowController().setCurrentDynamicFrameController(this);
        updateFrame();
    }

    @Override
    public void updateFrame() {

    }

    public void logout(ActionEvent event) {
        try {
            Main.getMainWindowController().requestsToServer.put("16");
            Thread.sleep(100);
            ServerConnection.getInstance().closeResources();
        } catch (InterruptedException e) {
            System.out.println("Exit interrupted");
        }
    }
}
