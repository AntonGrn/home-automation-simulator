package mainPackage.dynamicFrames;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import mainPackage.DynamicFrame;
import mainPackage.modelClasses.Account;


public class UsersController  implements DynamicFrame {

    @FXML
    private TableView<Account> usersTable;

    @FXML
    private TableColumn<Account, String> nameColumn, emailColumn;

    @FXML
    private TableColumn<Account, Boolean> accesslevelColumn;

    @FXML
    private Button submitButton, adminButton, nonAdminButton, deleteUserButton, addUserButton, editBtn;

    @FXML
    private PasswordField nonAdminPwField, adminPwField, passwordField;

    @FXML
    private AnchorPane tableAnchorPane, adminPassWd, nonAdminPassWd,  editUSer;

    @FXML
    private Label addOrEditUserLabel, statusLabel;

    @FXML
    private TextField nameField, emailField;

    @FXML
    private CheckBox checkboxAdmin;

    private ObservableList<Account> accountList = FXCollections.observableArrayList();
    private Account loggedInAccount;

    @FXML
    void initialize() {

        //Just to simulate a logged in account, and other accounts part of the same system
        Account a1 = new Account("Anton", "anton@mail.com", 1, true, "123");
        accountList.add(a1);
        accountList.add(new Account("Abdi", "abdi@mail.com", 1, true, "123"));
        accountList.add(new Account("Kalle", "kalle@mail.com", 1, false, "abc"));
        loggedInAccount = a1;

        //Set logged in account in Edit fields
        nameField.setText(loggedInAccount.getName());
        emailField.setText(loggedInAccount.getEmail());
        checkboxAdmin.fire();

        //Set defualt states
        deleteUserButton.setDisable(true);
        editBtn.setDisable(true);
        submitButton.setDisable(true);
        adminButton.setDisable(true);
        nonAdminButton.setDisable(true);
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: red;");
        submitButton.setUserData("Edit");

        usersTable.setItems(accountList);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        accesslevelColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));
        //PropertyValueFactory will look for getters

        // Add listener in the table, to perform actions when a user is selected
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        deleteUserButton.setDisable(false);
                        editBtn.setDisable(false);
                    } else {
                        deleteUserButton.setDisable(true);
                        editBtn.setDisable(true);
                    }
                });

    }

    //When a user has been selected for Edit
    @FXML
    void editChosen() {
        submitButton.setDisable(true);
        // Change label according to user
        if(accountList.get(
                usersTable.getSelectionModel().getFocusedIndex()).getEmail().equals(
                loggedInAccount.getEmail())) {
            addOrEditUserLabel.setText("Your info");
        } else {
            addOrEditUserLabel.setText("Edit: " + accountList.get(
                    usersTable.getSelectionModel().getFocusedIndex()).getName());
        }
        // Load data to text fields
        nameField.setText(accountList.get(
                usersTable.getSelectionModel().getFocusedIndex()).getName());
        emailField.setText(accountList.get(
                usersTable.getSelectionModel().getFocusedIndex()).getEmail());
        checkboxAdmin.setSelected((accountList.get(
                usersTable.getSelectionModel().getFocusedIndex()).isAdmin()));

    }

    //Clear table selection and buttons to: no user selected
    @FXML
    public void clearTableSelection() {
        editBtn.setDisable(true);
        deleteUserButton.setDisable(true);
        usersTable.getSelectionModel().clearSelection();

    }

    //When user text fields has been altered
    @FXML
    void editsMade() {
        submitButton.setDisable(false);
    }

    //When a user has been selected for Edit
    @FXML
    void addUser() {
        clearTableSelection();
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        checkboxAdmin.setSelected(false);
        addOrEditUserLabel.setText("Add new user");
    }

    @FXML
    public void submit() {
        //Check that no fields are empty
        if(nameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                passwordField.getText().trim().isEmpty()) {
            statusLabel.setText("Please fill out all fields");
        }

        if(submitButton.getUserData().equals("AddNewUser")) {
            //Get data from text fields and send request to server to add gedaget
        } else {
            //Get data from text fields and send request to alter existeing user data
        }

    }





    public void updateFrame() {

    }
}

