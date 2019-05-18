package mainPackage;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import mainPackage.modelClasses.*;
import mainPackage.modelClasses.Account;
import mainPackage.modelClasses.Gadget;
import mainPackage.modelClasses.Lamp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MainWindowController {

    @FXML
    private HBox menuFrame;

    @FXML
    private VBox centerBox;

    @FXML
    private Pane houseFrame, dynamicFrame;

    @FXML
    private GridPane loggedintatusFrame;

    @FXML
    private Button btnRooms, btnUsers, btnGadgets, btnEnergy, btnLogs, btnSettings;

    @FXML
    public Label exceptionLabel, loggedInLabel;

    private DynamicFrame currentDynamicFrameController;

    public ArrayList<Gadget> gadgetList;
    public ArrayList<String[]> logsList;
    public ArrayList<Account> accountList;

    //Producer-consumer pattern. Thread safe. Add requests to send to server.
    //Maybe have private, with getters
    //ADD: This should not be accessible when not logged in. Maybe add getter
    public BlockingQueue<String> requestsToServer;
    public BlockingQueue<String> requestsFromServer;

    //To notify the JavaFX-thread that updates has arrived from the Server.
    public BooleanProperty doUpdate;

    // For the houseFrame to know which room has been chosen by the user.
    public StringProperty chosenRoom;

    //Fake button for the enabling of loginScene
    private Button btnLogin;

    @FXML
    public void initialize() {
        //Set name of dynamic frame to which the button links
        btnRooms.setUserData("Rooms");
        btnUsers.setUserData("Users");
        btnGadgets.setUserData("Gadgets");
        btnEnergy.setUserData("Energy");
        btnLogs.setUserData("Logs");
        //temporary, shall be named setting later on
        btnSettings.setUserData("Test");
        btnLogin = new Button();
        btnLogin.setUserData("Login");
        btnLogin.setOnAction(this::setDynamicFrame);

        gadgetList = new ArrayList<>();
        logsList = new ArrayList<>();
        accountList = new ArrayList<>();
        requestsToServer = new ArrayBlockingQueue<>(10);
        requestsFromServer = new ArrayBlockingQueue<>(10);

        doUpdate = new SimpleBooleanProperty(false);

        chosenRoom = new SimpleStringProperty("null");

        //Until we can get Gadgets from Server:
        gadgetList.add(new Lamp("LampOne", false, 25, "Kitchen", 1));
        gadgetList.add(new Lamp("LampTwo", false, 25, "Kitchen", 2));
        gadgetList.add(new Lamp("LampThree", false, 25, "Bedroom", 3));

        //Loads the blueprint into the mainwindow HouseFrame
        setBlueprint();

        //Add listener to loggedInAccount object's loggedInAccountProperty
        AccountLoggedin.getInstance().loggedInAccountProperty().addListener(
                new ChangeListener<Account>() {
                    @Override
                    public void changed(ObservableValue<? extends Account> observable, Account oldValue, Account newValue) {
                        try {
                            isLoggedIn();
                        } catch (NullPointerException e) {  //If no account is held by the AccountLoggedin-object, newValue == null
                            isNotLoggedIn();
                        }
                    }
                }
        );

        //Since requests for updates from the Server is received by another thread than JavaFX, we need a way to notify the
        //JavaFX-Thread to process the new data that has arrived from the server.
        //Could also be done by an int, representing the number of updates to be done (if more arrives while processing)
        doUpdate.addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (doUpdate.getValue()) {

                            //Iin order to have this sun by FX thread, and not the thread issuing the doUpate.setValue(true).
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    //Update UI here
                                    update();
                                }
                            });
                            doUpdate.setValue(false);
                        }
                    }
                }
        );

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                isNotLoggedIn();
            }
        });
    }

    public void isLoggedIn() {
        //Code to execute when logged in
        loggedInLabel.setText("Logged in as  " + AccountLoggedin.getInstance().getLoggedInAccount().getName() + "  " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        //Request gadgets from server
        try {
            requestsToServer.put("5");
        } catch (InterruptedException e) {
            System.out.println("Unable to request gadgets at log in");
        }

        //Access to system according to if logged in account is admin or not
        for (Node node : menuFrame.getChildren()) {
            if (node instanceof Button) {
                if (AccountLoggedin.getInstance().getLoggedInAccount().isAdmin()) {
                    ((Button) node).setDisable(false);
                } else {
                    if(node.getUserData().equals("Rooms") ||
                            node.getUserData().equals("Energy") ||
                            node.getUserData().equals("Logs") ||
                            node.getUserData().equals("Settings")) {
                        ((Button) node).setDisable(false);
                    }

                }
            }
        }
        //Scene to load when logged in
        btnRooms.fire();
    }

    public void isNotLoggedIn() {
        //Code to execute when  not logged in, ex disable controls
        gadgetList.clear();
        logsList.clear();
        requestsToServer.clear();
        requestsFromServer.clear();

        loggedInLabel.setText("Not logged in");

        //Go to login screen
        btnLogin.fire();

        //Set all menu buttons to dosabled
        for (Node node : menuFrame.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setDisable(true);
            }
        }
    }

    public void setCurrentDynamicFrameController(DynamicFrame controller) {
        currentDynamicFrameController = controller;
    }

    @FXML
    void setDynamicFrame(ActionEvent event) {
        exceptionLabel.setText("");

        //Set all buttons to default layout.
        for (Node node : menuFrame.getChildren()) {
            if (node instanceof Button) {
                node.setStyle("");
            }
        }

        //Style the pressed button
        ((Button) event.getSource()).setStyle(
                "-fx-background-color: #346189;" +
                        "-fx-border-color:white;");

        //Perform scene change within the dynamicFrame of the MainWindow
        String url = ((Button) event.getSource()).getUserData().toString();

        try {
            dynamicFrame.getChildren().clear();
            dynamicFrame.getChildren().add(FXMLLoader.load(getClass().getResource("dynamicFrames/" + url + ".fxml")));
            //dynamicFrame.setLayoutX(100);
            //dynamicFrame.setLayoutY(0);
        } catch (IOException io) {
            io.printStackTrace();
            exceptionLabel.setText("Unable to load new scene.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            exceptionLabel.setText("Unable to load new scene.");
        }
    }

    //Adding blueprint to houseframe window
    public void setBlueprint() {
        try {
            houseFrame.getChildren().clear();
            houseFrame.getChildren().add(FXMLLoader.load(getClass().getResource("houseFrame/Blueprint.fxml")));
        } catch (IOException e) {

        }
    }

    //update() should be run by JavaFX-Thread, so should not be invoked by other threads (ex ClientInputThread)
    private void update() {
        exceptionLabel.setText("");
        //Update houseFrame + invoke update method of currentFrame
        try {
            String request = requestsFromServer.take();
            System.out.println(request);
            String[] commands = request.split(":");

            //Translate the requests according to the LAAS communication protocol:
            switch (commands[0]) {
                case "2": //Result of login attempt
                    loginAttempt(commands);
                    break;
                case "4": //Gadgets' states has been updated
                    updateGadgetsStates(commands);
                    break;
                case "8": //Gadgets info has been updated
                    updateGadgetsInfo(commands);
                    break;
                case "12": //Users' info has been updated
                    updateAccounts(commands);
                    break;
                case "14": //Log(s) has been received
                    updateLogs(commands);
                    break;
                case "15": //Exception message from server
                    exceptionLabel.setText(commands[1]);
                    break;
                case "17": //ClientInputThread lost connection with server
                    exceptionLabel.setText("Lost connection with server");
                    ServerConnection.getInstance().closeResources();
                    AccountLoggedin.getInstance().setLoggedInAccount(null);
                    break;
                default:
                    exceptionLabel.setText("Unknown update request received");
            }
        } catch (InterruptedException e) {
            exceptionLabel.setText("Unable to update from server");
        }
        currentDynamicFrameController.updateFrame();

        for(Gadget g : gadgetList) {
            System.out.println(g.getName());
        }

    }
    private void updateGadgetsStates(String[] commands) {
        if (commands[1].equals("notnull")) {
            int count = 1;
            while (true) {
                int gadgetID = Integer.parseInt(commands[count + 1]);
                int state = Integer.parseInt(commands[count + 2]);
                for (Gadget gadget : Main.getMainWindowController().gadgetList) {
                    if (gadget.getId() == gadgetID) {
                        if (gadget instanceof Heat) {
                            ((Heat) gadget).setState(state);
                        } else {
                            //We allow the unchecked warning, knowing all thees gadget types has boolean state variables.
                            gadget.setState(state == 1);
                        }
                    }
                }
                if (commands[count + 3].equals("null")) {
                    break;
                }
                count += 3;
            }
        }
    }

    private void updateGadgetsInfo(String[] commands) {
        gadgetList.clear();
        if (commands[1].equals("notnull")) {
            int count = 1;
            while (true) {
                String gadgetType = commands[count + 1];
                int gadgetID = Integer.parseInt(commands[count + 2]);
                String gadgetName = commands[count + 3];
                String gadgetRoom = commands[count + 4];
                int gadgetState = Integer.parseInt(commands[count + 5]);
                int gadgetConsumption = Integer.parseInt(commands[count + 6]);

                Gadget gadget = null;
                switch (gadgetType) {
                    case "Door":
                        gadget = new Door(gadgetName, gadgetState == 1, gadgetConsumption, gadgetRoom, gadgetID);
                        break;
                    case "Heat":
                        gadget = new Heat(gadgetName, gadgetState, gadgetConsumption, gadgetRoom, gadgetID);
                        break;
                    case "Lamp":
                        gadget = new Lamp(gadgetName, gadgetState == 1, gadgetConsumption, gadgetRoom, gadgetID);
                        break;
                    case "Multimedia":
                        gadget = new Multimedia(gadgetName, gadgetState == 1, gadgetConsumption, gadgetRoom, gadgetID);
                        break;
                }
                gadgetList.add(gadget);

                if (commands[count + 7].equals("null")) {
                    break;
                }
                count += 7;
            }
        }
    }

    private void updateAccounts(String[] commands) {
        accountList.clear();
        int count = 0;
        while (true) {
            String accountID = commands[count + 1];
            String name = commands[count + 2];
            String password = commands[count + 3];
            boolean admin = commands[count + 4].equals("1");

            Account a1 = new Account(name, accountID, AccountLoggedin.getInstance().getLoggedInAccount().getSystemID(), admin, password );
            accountList.add(a1);
            if (commands[count + 5].equals("null")) {
                break;
            }
            count += 5;
        }
    }

    private void updateLogs(String[] commands) {
        logsList.clear();
        int count = 0;
        while (true) {
            String timestamp = commands[count + 1].replace("&", ":"); // Reformat to regain colon in timestamp
            String logMessage = commands[count + 2];

            String[] log = {timestamp, logMessage};
            logsList.add(log);
            if (commands[count + 3].equals("null")) {
                break;
            }
            count += 3;
        }
    }

    private void loginAttempt(String[] commands) {
        if (commands[1].equals("ok")) {
            //Create account and send it as parameter to: AccountLoggedin.getInstance().setLoggedInAccount(account);
            String accountID = commands[3];
            String systemID = commands[4];
            String name = commands[5];
            String admin = commands[6];
            String password = commands[7];

            Account a1 = new Account(name, accountID, Integer.parseInt(systemID), (admin.equals("1") ? true : false), password);
            AccountLoggedin.getInstance().setLoggedInAccount(a1);

        } else if (commands[1].equals("no")) {
            exceptionLabel.setText(commands[2]);
            AccountLoggedin.getInstance().setLoggedInAccount(null);
        } else {
            exceptionLabel.setText("Login failed for unknown reasons");
            AccountLoggedin.getInstance().setLoggedInAccount(null);
        }
    }

}
