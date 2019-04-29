package mainPackage.dynamicFrames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import mainPackage.*;

public class TestController implements DynamicFrame {

    @FXML
    public void initialize() {
        Main.getMainWindowController().setCurrentDynamicFrameController(this);
    }

    public void updateFrame() {

    }

}
