package mainPackage.houseFrame;

import mainPackage.DynamicFrame;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import mainPackage.DynamicFrame;
import mainPackage.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class BlueprintController implements DynamicFrame, Initializable {


    @FXML
    private Label toilet;

    @FXML
    private Label bedroom;

    @FXML
    private Label livingroom;

    @FXML
    private Label garage;

    @FXML
    private Label kitchen;

    @FXML
    private AnchorPane blueprint;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (Node node : blueprint.getChildren()) {
            if (node instanceof Label) {
                node.setVisible(false);
                node.setStyle("");
            }
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Main.getMainWindowController().chosenRoom.addListener(
                        new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                String chosenRoom = newValue;
                                changeColor(chosenRoom);
                            }
                        }
                );

            }
        });



    }

    @Override
    public void updateFrame() {

    }

    @FXML
    public void changeColor(String chosenRoom) {
        for (Node node : blueprint.getChildren()) {
            if (node instanceof Label) {
                node.setVisible(false);
                node.setStyle("");
            }
        }
        for (Node node : blueprint.getChildren()) {
            if (node instanceof Label) {
                if (node.getId().equalsIgnoreCase(chosenRoom)) {
                    node.setVisible(true);
                    node.setStyle("-fx-text-fill: #1e97d2");
                }
            }
        }
    }
}
