package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import mainPackage.Account;
import mainPackage.AccountLoggedin;
import mainPackage.DynamicFrame;

public class TestController implements DynamicFrame {

    @FXML
    public void initialize() {
        //Simulate a login:
        Account a1 = new Account("Anton");
        AccountLoggedin.getInstance().setLoggedInAccount(a1);
    }

    public void updateFrame() {

    }

}
