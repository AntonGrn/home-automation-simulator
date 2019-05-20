package mainPackage.dynamicFrames;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import mainPackage.AccountLoggedin;
import mainPackage.DynamicFrame;
import mainPackage.Main;
import mainPackage.modelClasses.Account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UsersController implements DynamicFrame {

    @FXML
    private TableView<Account> usersTable;

    @FXML
    private TableColumn<Account, String> nameColumn, emailColumn;

    @FXML
    private TableColumn<Account, Boolean> accesslevelColumn;

    @FXML
    private Button submitButton, submitNonAdminPassW, deleteUserButton, addUserButton, editBtn;

    @FXML
    private PasswordField nonAdminPwField, passwordField;

    @FXML
    private AnchorPane tableAnchorPane, nonAdminPassWd, editUSer;

    @FXML
    private Label addOrEditUserLabel, statusLabel;

    @FXML
    private TextField nameField, emailField;

    @FXML
    private CheckBox checkboxAdmin;

    private ObservableList<Account> accountList;
    private boolean addUser;

    @FXML
    void initialize() {

        Main.getMainWindowController().setCurrentDynamicFrameController(this);

        addUserButton.fire();

        //Set defualt states
        deleteUserButton.setDisable(true);
        editBtn.setDisable(true);
        submitButton.setDisable(true);
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: red;");
        submitButton.setUserData("Edit");
        passwordField.setDisable(true);
        submitNonAdminPassW.setDisable(true);

        updateFrame();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        accesslevelColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));
        //PropertyValueFactory will look for getters

        // Add listener in the table, to perform actions when a user is selected
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {

                        if (newSelection.isAdmin() & !newSelection.getEmail().equals(AccountLoggedin.getInstance().getLoggedInAccount().getEmail())) {
                            editBtn.setDisable(true);
                            deleteUserButton.setDisable(true);
                        } else {
                            editBtn.setDisable(false);
                            deleteUserButton.setDisable(false);
                        }
                    } else {
                        deleteUserButton.setDisable(true);
                        editBtn.setDisable(true);
                    }
                });

        checkboxAdmin.selectedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            passwordField.setDisable(false);
                        } else {
                            passwordField.setDisable(true);
                        }
                    }
                }
        );

        try {
            Main.getMainWindowController().requestsToServer.put("9");
        } catch (InterruptedException e) {
            Main.getMainWindowController().exceptionLabel.setText("Server request failed");
        }
    }

    public void updateFrame() {
        accountList = FXCollections.observableArrayList(Main.getMainWindowController().accountList);
        usersTable.getItems().clear();
        usersTable.setItems(accountList);
    }

    //When a user has been selected for Edit
    @FXML
    void editChosen() {
        Account account = accountList.get(usersTable.getSelectionModel().getFocusedIndex());

        statusLabel.setText("");
        submitButton.setDisable(true);
        // Change label according to user
        if (account.getEmail().equals(
                AccountLoggedin.getInstance().getLoggedInAccount().getEmail())) {
            addOrEditUserLabel.setText("Your info");
            //Editing users existing profile, not adding new
            addUser = false;
        } else {
            addOrEditUserLabel.setText("Edit: " + accountList.get(
                    usersTable.getSelectionModel().getFocusedIndex()).getName());
        }
        // Load data to text fields
        nameField.setText(account.getName());
        emailField.setText(account.getEmail());
        checkboxAdmin.setSelected(account.isAdmin());
    }

    //Clear table selection and buttons to: no user selected
    @FXML
    public void clearTableSelection() {
        editBtn.setDisable(true);
        deleteUserButton.setDisable(true);
        usersTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void viewNonAdminPassWord() {
        nonAdminPwField.setPromptText(getNonAdminPassW());
    }

    @FXML
    public void hideNonAdminPassWord() {
        nonAdminPwField.setPromptText("Password");
    }

    private String getNonAdminPassW() {
        String passW = "No non-admin password";
        for (Account a1 : Main.getMainWindowController().accountList) {
            if (!a1.isAdmin()) {
                passW = a1.getPassword();
                break;
            }
        }
        return passW;
    }

    //When user text fields has been altered
    @FXML
    void editsMade() {
        submitButton.setDisable(false);
    }

    //When user text fields has been altered
    @FXML
    void nonAdminPassWEditsMade() {
        submitNonAdminPassW.setDisable(false);
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
        addUser = true;
    }

    @FXML
    public void submit() {
        statusLabel.setText("");
        //Check that no fields are empty
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() ||
                (checkboxAdmin.isSelected() & passwordField.getText().trim().isEmpty())) {
            statusLabel.setText("Please fill out all fields");
        } else if (nameField.getText().contains(":") || emailField.getText().contains(":") ||
                (checkboxAdmin.isSelected() & passwordField.getText().contains(":"))) {
            statusLabel.setText("It should not contain colon ' : '");
        } else if (!emailField.getText().contains("@") || !emailField.getText().contains(".")) {
            statusLabel.setText("Example@homeautomation.se");
        } else {
            //Get data from text fields and send request to server to add gedaget
            String email = emailField.getText();
            String name = nameField.getText();
            String admin = checkboxAdmin.isSelected() ? "1" : "0";
            String password;
            if (checkboxAdmin.isSelected()) {
                password = passwordField.getText();
            } else {
                password = getNonAdminPassW();
            }
            if (addUser) {
                try {
                    // Form a proper request, according to communiaction protocolrd
                    String serverRequest = String.format("%s%s%s%s%s%s%s%s", "11a:", email, ":", name, ":", admin, ":", password);
                    //Add request to requestsToServer
                    Main.getMainWindowController().requestsToServer.put(serverRequest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //Alter existing account info
                try {
                    String serverRequest = String.format("%s%s%s%s%s%s%s%s%s%s", "10:", email, ":", name, ":", admin, ":", password, ":", "keep");
                    Main.getMainWindowController().requestsToServer.put(serverRequest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean validateEmal() {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$");
        Matcher matcher = pattern.matcher(nameField.getText());

        if (matcher.find() && matcher.group().equals(nameField.getText())) {
            return true;
        } else {
            return false;
        }
    }

    @FXML
    void deleteUser() {
        String accountID = accountList.get(usersTable.getSelectionModel().getFocusedIndex()).getEmail();
        try {
            String serverRequest = String.format("%s%s", "11b:", accountID);
            Main.getMainWindowController().requestsToServer.put(serverRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

