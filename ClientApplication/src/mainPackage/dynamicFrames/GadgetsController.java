package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mainPackage.DynamicFrame;

public class GadgetsController implements DynamicFrame {

    @FXML
    private TableColumn<?, ?> clmType;

    @FXML
    private ToggleGroup gadgetGroup;

    @FXML
    private TableColumn<?, ?> clmRoom;

    @FXML
    private Button btnUpdate;

    @FXML
    private RadioButton radioGarage;

    @FXML
    private ToggleGroup roomGroup;

    @FXML
    private TableView<?> tblViewCurrentGadgets;

    @FXML
    private RadioButton radioDoor;

    @FXML
    private RadioButton radioHeat;

    @FXML
    private RadioButton radioKitchen;

    @FXML
    private RadioButton radioToilet;

    @FXML
    private RadioButton radioBedroom;

    @FXML
    private Button btnDelete;

    @FXML
    private RadioButton radioLamp;

    @FXML
    private Button btnAdd;

    @FXML
    private TextField txtfldAddGadget;

    @FXML
    private RadioButton radioMultimedia;

    @FXML
    private TableColumn<?, ?> clmId;

    @FXML
    private RadioButton radioLivingroom;


    @Override
    public void updateFrame() {

    }
}
