package mainPackage.dynamicFrames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import mainPackage.Main;
import mainPackage.ServerConnection;
import mainPackage.modelClasses.Account;
import mainPackage.AccountLoggedin;
import mainPackage.DynamicFrame;
import mainPackage.modelClasses.Lamp;

import java.awt.*;

public class TestController implements DynamicFrame {

    @FXML
    public void initialize() {
        //Simulate a login:
        Account a1 = new Account("Anton");
        AccountLoggedin.getInstance().setLoggedInAccount(a1);
    }

    @FXML
    public void login(ActionEvent event) {
        ServerConnection client = new ServerConnection();
        try {
            client.connectToServer();
            String name = "Anton";
            String password = "123";
            String serverRequest = String.format("%s%s%s%s","1:", name, ":", password);
            Main.getMainWindowController().requestsToServer.put(serverRequest);
        } catch (Exception e) {
            System.out.println("Unable to connect to server");
            System.out.println(e.getMessage());
        }

    }

    public void updateFrame() {

    }

}
