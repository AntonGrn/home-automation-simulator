package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import mainPackage.DynamicFrame;
import mainPackage.Main;
import mainPackage.MainWindowController;
import mainPackage.modelClasses.Gadget;

public class GadgetsController implements DynamicFrame {

    @FXML
    private TableColumn<Gadget, String> clmType;
    @FXML
    private TableColumn<Gadget, String> clmRoom;
    @FXML
    private TableColumn<Gadget, String> clmId;
    @FXML
    private TableView<Gadget> tblViewCurrentGadgets;

    @FXML
    private ToggleGroup gadgetGroup;
    @FXML
    private ToggleGroup roomGroup;

    @FXML
    private Button btnUpdate;
    @FXML
    private RadioButton radioGarage;
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
    private RadioButton radioLivingroom;



    public void initialize(){
        updateFrame();
    }

    @Override
    public void updateFrame() {
        clmId.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        clmType.setCellValueFactory(new PropertyValueFactory<>("name"));
        System.out.println("Hello");

        tblViewCurrentGadgets.getItems().addAll(Main.getMainWindowController().gadgetList);

    }
}
