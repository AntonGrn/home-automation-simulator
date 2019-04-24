package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import mainPackage.Main;
import mainPackage.modelClasses.Account;
import mainPackage.AccountLoggedin;
import mainPackage.DynamicFrame;
import mainPackage.modelClasses.Lamp;

import java.awt.*;

public class TestController implements DynamicFrame {

    @FXML
    public void initialize() {
        //Simulate a login:
        Account a1 = new Account("Anton", "anton@mail.com", 1, "admin","123");
        AccountLoggedin.getInstance().setLoggedInAccount(a1);
    }

    public void updateFrame() {

    }

}
