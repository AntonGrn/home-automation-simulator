package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import mainPackage.DynamicFrame;
import mainPackage.Main;

public class LogsController implements DynamicFrame {

    @FXML
    private VBox logsContainer;

    @FXML
    public void initialize() {

        Main.getMainWindowController().setCurrentDynamicFrameController(this);

        try {
            Main.getMainWindowController().requestsToServer.put("11");
        } catch (InterruptedException e) {
            Main.getMainWindowController().exceptionLabel.setText("Server request failed");
        }
        updateFrame();
    }


    public void updateFrame() {
        logsContainer.getChildren().clear();
        //For every log in logList, add a new label displaying this log
        for (String[] logs : Main.getMainWindowController().logsList) {
            String timestamp = logs[0];
            String logMessage = logs[1];
            Label label = new Label(timestamp + "     " + logMessage);
            logsContainer.getChildren().add(label);
        }

    }
}
