package mainPackage.houseFrame;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import mainPackage.DynamicFrame;

import java.net.URL;
import java.util.ResourceBundle;

public class BlueprintController implements DynamicFrame, Initializable {
    @Override
    public void updateFrame() {

    }

    @FXML
    public Label toilet;

    @FXML
    public Label bedroom;

    @FXML
    public Label livingroom;

    @FXML
    public Label garage;

    @FXML
    public  Label kitchen;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toilet.setVisible(false);
        bedroom.setVisible(false);
        livingroom.setVisible(false);
        garage.setVisible(false);
        kitchen.setVisible(false);

        toilet.getStyleClass().remove("label");
        bedroom.getStyleClass().remove("label");
        livingroom.getStyleClass().remove("label");
        garage.getStyleClass().remove("label");
        kitchen.getStyleClass().remove("label");

    }
}
