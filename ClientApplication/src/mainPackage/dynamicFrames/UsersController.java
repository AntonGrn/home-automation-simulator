package mainPackage.dynamicFrames;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import mainPackage.DynamicFrame;

public class UsersController implements DynamicFrame {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane tableAnchorPane;

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> emailColumn;

    @FXML
    private TableColumn<?, ?> accesslevelColumn;

    @FXML
    private Button deleteUserButton;

    @FXML
    private Button addUserButton;

    @FXML
    private AnchorPane nonAdminPassWd;

    @FXML
    private PasswordField nonAdminPwField;

    @FXML
    private Button nonAdminButton;

    @FXML
    private AnchorPane adminPassWd;

    @FXML
    private PasswordField adminPwField;

    @FXML
    private Button adminButton;

    @FXML
    private AnchorPane editUSer;

    @FXML
    private Label addOrEditUserLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox checkboxAdmin;

    @FXML
    private Button submitButton;

    @FXML
    void initialize() {
        String name  = nameField.getText();
        String email = emailField.getText();
        String pass  = passwordField.getText();
        String accessLevel = checkboxAdmin.getText();

        if (name.contains(":") | email.contains(":") | pass.contains(":")){

        }
    }

    @Override
    public void updateFrame() {

    }
}
