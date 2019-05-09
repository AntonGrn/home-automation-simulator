package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import mainPackage.DynamicFrame;
import mainPackage.Main;

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
}
