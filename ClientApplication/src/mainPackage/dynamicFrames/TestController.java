package mainPackage.dynamicFrames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import mainPackage.Main;
import mainPackage.ServerConnection;
import mainPackage.modelClasses.Account;
import mainPackage.AccountLoggedin;
import mainPackage.DynamicFrame;

public class TestController implements DynamicFrame {

    @FXML
    public void initialize() {

        Main.getMainWindowController().setCurrentDynamicFrameController(this);

        //Simulate a login:
        Account a1 = new Account("Anton", "anton@mail.com", 1, "admin", "123");
        AccountLoggedin.getInstance().setLoggedInAccount(a1);
    }

/*    @FXML
    public void login(ActionEvent event) {
        try {
            //Request server connection
            ServerConnection.getInstance().connectToServer();
            //Form a proper log in request, according to communication protocol: 1:accountID:password
            String accountID = "anton_goransson@hotmail.com"; //This would be fetched from text field
            String password = "123";                          //This would be fetched from text field
            String serverRequest = String.format("%s%s%s%s","1:", accountID, ":", password);
            //Add request to requestsToServer
            Main.getMainWindowController().requestsToServer.put(serverRequest);
        } catch (Exception e) {
            Main.getMainWindowController().setExceptionLabel(e.getMessage());
        }

    }*/


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
