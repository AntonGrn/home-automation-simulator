package mainPackage;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private Button btn1, btn2, btn3;

    @FXML
    private Label exceptionLabel, loggedInLabel;

    private String currentDynamicFrame;

    public ArrayList<Gadget> gadgetList;
    // When model class Room is added: private ArrayList<Room> roomList;
    // Or:                             public ArrayList<Pane> roomList;

    //Producer-consumer pattern. Thread safe. Add requests to send to server.
    public BlockingQueue<String> serverRequests;

    @FXML
    public void initialize() {

        //Set name of dynamic frame to which the button links
        btn1.setUserData("Test");
        btn2.setUserData("RoomsController");
        btn3.setUserData("testFrame");

        gadgetList = new ArrayList<>();
        serverRequests = new ArrayBlockingQueue<>(10);

        //Until we can get Gadgets from Server:
        gadgetList.add(new Lamp("LampOne", 25, "Kitchen"));
        gadgetList.add(new Lamp("LampTwo", 25, "Kitchen"));

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
    }

    public void isLoggedIn() {
        //Code to execute when logged in
        loggedInLabel.setText("Logged in as  " + AccountLoggedin.getInstance().getLoggedInAccount().getName() + "  " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    public void isNotLoggedIn() {
        //Code to execute when  not logged in, ex disable controls
        loggedInLabel.setText("Not logged in");
        //+ Set dynamic frame to log in frame
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
                "-fx-background-color: orange;" +
                        "-fx-border-color:white;");

        //Perform scene change within the dynamicFrame of the MainWindow
        String url = ((Button) event.getSource()).getUserData().toString();

        try {
            dynamicFrame.getChildren().clear();
            dynamicFrame.getChildren().add(FXMLLoader.load(getClass().getResource("dynamicFrames/" + url + ".fxml")));
            //dynamicFrame.setLayoutX(100);
            //dynamicFrame.setLayoutY(0);
            currentDynamicFrame = url;
        } catch (IOException io) {
            exceptionLabel.setText("Unable to load new scene.");
        } catch (NullPointerException e) {
            exceptionLabel.setText("Unable to load new scene.");
        }
    }

    public synchronized void update() { //Can be accessed by client thread, and main thread
        //Should it be synchronized code block instead, since other threads access other methods in this class???
        //Update houseFrame + invoke update method of currentFrame
    }

}
