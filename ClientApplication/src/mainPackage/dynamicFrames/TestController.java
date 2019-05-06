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
    ProgressIndicator progress;

    @FXML
    public void initialize() {

        Main.getMainWindowController().setCurrentDynamicFrameController(this);

        progress.setStyle("-fx-progress-color: grey;");
    }

    @FXML
    public void login(ActionEvent event) {
        try {
            //Request server connection
            ServerConnection.getInstance().connectToServer();
            String accountID = "anton_goransson@hotmail.com"; //This would be fetched from text field
            String password = "123";                          //This would be fetched from text field
            //Form a proper login request, according to communication protocol: 1:accountID:password
            String serverRequest = String.format("%s%s%s%s","1:", accountID, ":", password);
            //Add request to requestsToServer
            Main.getMainWindowController().requestsToServer.put(serverRequest);
        } catch (Exception e) {
            AccountLoggedin.getInstance().setLoggedInAccount(null);
            Main.getMainWindowController().exceptionLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void logout (ActionEvent event) {
            try {
                Main.getMainWindowController().requestsToServer.put("14");
                Thread.sleep(100);
                ServerConnection.getInstance().closeResources();
            }catch (InterruptedException e) {
                System.out.println("Exit interrupted");
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
    }
}
