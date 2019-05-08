package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import mainPackage.DynamicFrame;

public class SettingsController implements DynamicFrame {

    @FXML
    private Button btnLogOut;

    @FXML
    private TextArea textAreaInfoAboutUs;

    public void initialize(){
    updateFrame();
    }

    @Override
    public void updateFrame() {

    }
}
