package mainPackage.dynamicFrames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import mainPackage.*;

public class TestController implements DynamicFrame {


    @FXML
    public void initialize() {

        Main.getMainWindowController().setCurrentDynamicFrameController(this);
    }

    @FXML
    public void login(ActionEvent event) {
        ServerConnection client = new ServerConnection();
        try {
            //Request server connection
            client.connectToServer();
            //Form a proper log in request, according to communication protocol: 1:accountID:password
            String accountID = "anton_goransson@hotmail.com"; //This would be fetched from text field
            String password = "123";                          //This would be fetched from text field
            String serverRequest = String.format("%s%s%s%s","1:", accountID, ":", password);
            //Add request to requestsToServer
            Main.getMainWindowController().requestsToServer.put(serverRequest);
        } catch (Exception e) {
            Main.getMainWindowController().setExceptionLabel(e.getMessage());
        }

    }

    public void updateFrame() {

    }

}
