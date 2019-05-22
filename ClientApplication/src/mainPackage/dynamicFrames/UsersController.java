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
    private Button btnSubmitA, btnAddUser, btnEdit, btnSubmitNonAdminPassword, btnCancelNonAdminPasswordEdit, btnCancelEditUser, btnViewAdminPassword;

    @FXML
    private PasswordField nonAdminPasswordField, adminPasswordField;

    @FXML
    private AnchorPane tableAnchorPane, nonAdminPassWd, editUSer;

    @FXML
    private Label addOrEditUserLabel, statusLabel, deleteLabel;

    @FXML
    private TextField nameField, emailField;

    @FXML
    private CheckBox checkboxAdmin, checkboxDeleteUser;

    private ObservableList<Account> accountList;
    private boolean addUser;
    private String nonAdminPW;
    private String usersPW;

    @FXML
    void initialize() {

        Main.getMainWindowController().setCurrentDynamicFrameController(this);

        //Set defualt states
        btnEdit.setDisable(true);
        btnSubmitA.setDisable(true);
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: red;");
        deleteLabel.setStyle("-fx-text-fill: red;");
        checkboxDeleteUser.setStyle("-fx-text-fill: white;");
        deleteLabel.setVisible(false);
        btnSubmitA.setUserData("Edit");
        adminPasswordField.setDisable(true);

        btnAddUser.fire();

        updateFrame();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        accesslevelColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));

        //Display boolean values as Yes/No in table
        accesslevelColumn.setCellFactory(col -> new TableCell<Account, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty) ;
                setText(empty ? null : item ? "Yes" : "No" );
            }
        });

        // Add listener in the table, to perform actions when a user is selected
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {

                        if (newSelection.isAdmin() & !newSelection.getEmail().equals(AccountLoggedin.getInstance().getLoggedInAccount().getEmail())) {
                            btnEdit.setDisable(true);
                            //deleteUserButton.setDisable(true);
                        } else {
                            btnEdit.setDisable(false);
                            //deleteUserButton.setDisable(false);
                        }
                    } else {
                        //deleteUserButton.setDisable(true);
                        btnEdit.setDisable(true);
                    }
                });

        checkboxAdmin.selectedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        //if admin
                        if (newValue) {
                            adminPasswordField.setDisable(false);
                            btnViewAdminPassword.setDisable(false);
                            if(addUser) {
                                statusLabel.setText("New admin accounts can only be managed by that account");
                            }
                            //if not admin
                        } else {
                            adminPasswordField.setDisable(true);
                            btnViewAdminPassword.setDisable(true);
                            statusLabel.setText("");
                        }
                    }
                }
        );

        checkboxDeleteUser.selectedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            deleteLabel.setVisible(true);
                            emailField.setDisable(true);
                            nameField.setDisable(true);
                            checkboxAdmin.setDisable(true);
                            adminPasswordField.setDisable(true);
                            btnSubmitA.setDisable(false);
                        } else {
                            deleteLabel.setVisible(false);
                            emailField.setDisable(false);
                            nameField.setDisable(false);
                            checkboxAdmin.setDisable(false);
                            adminPasswordField.setDisable(false);
                            btnSubmitA.setDisable(true);
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
        nonAdminPW = getNonAdminPassword();
        resetNonAdminPasswordEdit();
    }

    //When a user has been selected for Edit
    @FXML
    void editChosen() {
        Account account = accountList.get(usersTable.getSelectionModel().getFocusedIndex());
        checkboxDeleteUser.setVisible(true);
        checkboxDeleteUser.setSelected(false);
        btnCancelEditUser.setDisable(true);
        addUser = false;
        usersPW = account.getPassword();

        statusLabel.setText("");
        btnSubmitA.setDisable(true);
        // Change label according to user
        if (account.getEmail().equals(
                AccountLoggedin.getInstance().getLoggedInAccount().getEmail())) {
            addOrEditUserLabel.setText("Your info");
            //Editing users existing profile, not adding new

        } else {
            addOrEditUserLabel.setText("Edit: " + account.getName());
            //adminPasswordField.clear();
            //adminPasswordField.setText(account.getPassword());
        }
        adminPasswordField.clear();
        adminPasswordField.setText(usersPW);
        // Load data to text fields
        nameField.setText(account.getName());
        emailField.setText(account.getEmail());
        checkboxAdmin.setSelected(account.isAdmin());
    }

    //Clear table selection and buttons to: no user selected
    @FXML
    public void clearTableSelection() {
        btnEdit.setDisable(true);
        usersTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void viewNonAdminPassword() {
        nonAdminPasswordField.clear();
        nonAdminPasswordField.setPromptText(nonAdminPW);
        nonAdminPassWd.requestFocus();
    }

    @FXML
    public void hideNonAdminPassword() {
        nonAdminPasswordField.setText(nonAdminPW);
    }

    //When user text fields has been altered
    @FXML
    void nonAdminPasswordEditsMade() {
        if(nonAdminPasswordField.getText().trim().isEmpty()) {
            btnSubmitNonAdminPassword.setDisable(true);
        }else {
            btnSubmitNonAdminPassword.setDisable(false);
            btnCancelNonAdminPasswordEdit.setDisable(false);
        }
        nonAdminPW = nonAdminPasswordField.getText();
    }

    @FXML
    public void resetNonAdminPasswordEdit() {
        nonAdminPW = getNonAdminPassword();
        nonAdminPasswordField.clear();
        nonAdminPasswordField.setText(nonAdminPW);
        btnSubmitNonAdminPassword.setDisable(true);
        btnCancelNonAdminPasswordEdit.setDisable(true);
    }

    @FXML
    public void submitNonAdminPasswordEdits() {
        if(nonAdminPasswordField.getText().trim().isEmpty()) {
            statusLabel.setText("Field empty");
        } else if(nonAdminPasswordField.getText().contains(":")) {
            statusLabel.setText("Password may not contain colon");
        } else {
            try {
                String serverRequest = String.format("%s%s", "10b:", nonAdminPasswordField.getText());
                Main.getMainWindowController().requestsToServer.put(serverRequest);
            } catch (InterruptedException e) {
                statusLabel.setText("Unable to edit non-admin password");
            }
        }
    }

    //Collect current non-admin password
    private String getNonAdminPassword() {
        String passW = "";
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
        btnSubmitA.setDisable(false);
        btnCancelEditUser.setDisable(false);
        usersPW = adminPasswordField.getText();
    }

    @FXML
    public void viewUsersPassword() {
        adminPasswordField.clear();
        adminPasswordField.setPromptText(usersPW);
        nonAdminPassWd.requestFocus();

        //nonAdminPasswordField.setDisable(true);
    }

    @FXML
    public void hideUsersPassword() {
       adminPasswordField.setText(usersPW);
    }

    //Set the GUI correct for adding a user
    @FXML
    void addUser() {
        clearTableSelection();
        nameField.clear();
        emailField.clear();
        adminPasswordField.clear();
        adminPasswordField.setPromptText("");
        checkboxAdmin.setSelected(false);
        checkboxDeleteUser.setVisible(false);
        checkboxDeleteUser.setSelected(false);
        btnCancelEditUser.setDisable(true);
        addOrEditUserLabel.setText("Add new user");
        usersPW = "";
        addUser = true;
    }

    @FXML
    public void submit() {
        statusLabel.setText("");
        //DELETE USER
        if (checkboxDeleteUser.isSelected()) {
            try {
                Main.getMainWindowController().requestsToServer.put("11b:" + accountList.get(usersTable.getSelectionModel().getFocusedIndex()).getEmail());
            } catch (InterruptedException e) {
                statusLabel.setText("Unable to delete user");
            }
            addUser();
        } else {
            //Check that no fields are empty, does not contain colon and that email conforms correctly
            if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() ||
                    (checkboxAdmin.isSelected() & adminPasswordField.getText().trim().isEmpty())) {
                statusLabel.setText("Please fill out all fields");
            } else if (nameField.getText().contains(":") || emailField.getText().contains(":") ||
                    (checkboxAdmin.isSelected() & adminPasswordField.getText().contains(":"))) {
                statusLabel.setText("Fields may not contain colon");
            } else if (!emailField.getText().contains("@") || !emailField.getText().contains(".")) {
                statusLabel.setText("Invalid email format");
            } else {
                //Get data from text fields and send request to server to add gedaget
                String email = emailField.getText();
                String name = nameField.getText();
                String admin = checkboxAdmin.isSelected() ? "1" : "0";
                String password;
                if (checkboxAdmin.isSelected()) {
                    password = adminPasswordField.getText();
                } else {
                    password = getNonAdminPassword();
                }
                //ADD USER
                if (addUser) {
                    try {
                        // Form a proper request, according to communication protocol
                        String serverRequest = String.format("%s%s%s%s%s%s%s%s", "11a:", email, ":", name, ":", admin, ":", password);
                        //Add request to requestsToServer
                        Main.getMainWindowController().requestsToServer.put(serverRequest);
                    } catch (InterruptedException e) {
                        statusLabel.setText("Unable to add user");
                    }
                    //EDIT USER INFO
                } else {
                    try {
                        String serverRequest = String.format("%s%s%s%s%s%s%s%s%s%s", "10a:", email, ":", name, ":", admin, ":", password, ":", "keep");
                        Main.getMainWindowController().requestsToServer.put(serverRequest);
                    } catch (InterruptedException e) {
                        statusLabel.setText("Unable to edit user");
                    }
                }
                addUser();
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

}

