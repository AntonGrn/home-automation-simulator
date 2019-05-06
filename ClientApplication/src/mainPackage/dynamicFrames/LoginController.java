package mainPackage.dynamicFrames;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mainPackage.DynamicFrame;
import mainPackage.Main;

public class LoginController implements DynamicFrame {

    @FXML
    private TextField usernametextfield;

    @FXML
    private PasswordField passfield;

    @FXML
    private Label inputErrorLabel;

    @FXML
    public void initialize() {
        Main.getMainWindowController().setCurrentDynamicFrameController(this);
        inputErrorLabel.setVisible(false);
        inputErrorLabel.setStyle("-fx-text-fill: red;");
    }

    @FXML
    void login(ActionEvent event) {
        inputErrorLabel.setVisible(false);
        String accountID = usernametextfield.getText();
        // + Make sure user does not type any colon (":")
        String password = passfield.getText();
        // + Make sure user does not type any colon (":")

        //Assure accountID and password does not contain colon
        String regex = "^(?:.*:).+$";
        if (accountID.matches(regex) || password.matches(regex)) {
            inputErrorLabel.setText("Name and password may not contain colon");
            inputErrorLabel.setVisible(true);
        } else {
            try {
                // Form a proper login request, according to communiaction protocol: 1:accountID:password
                String serverRequest = String.format("%s%s%s%s", "1:", accountID, ":", password);
                //Add request to requestsToServer
                Main.getMainWindowController().requestsToServer.put(serverRequest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateFrame() {

    }
}