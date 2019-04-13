package mainPackage;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AccountLoggedin {

    private ObjectProperty<Account> loggedInAccount = new SimpleObjectProperty<>(this, "loggedInAccount", null);

    private AccountLoggedin(){}

    private static AccountLoggedin instance = null;

    public static AccountLoggedin getInstance() {
        if (instance == null) {
            instance = new AccountLoggedin();
        }
        return instance;
    }

    public Account getLoggedInAccount() {
        return loggedInAccount.get();
    }

    public void setLoggedInAccount(Account loggedInAccount) {
        this.loggedInAccount.set(loggedInAccount);
    }

    //This is used when adding listener and alike.
    public ObjectProperty<Account> loggedInAccountProperty() {
        return loggedInAccount;
    }

}
