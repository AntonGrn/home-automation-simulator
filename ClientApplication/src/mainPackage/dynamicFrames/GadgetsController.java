package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mainPackage.DynamicFrame;
import mainPackage.Main;
import mainPackage.modelClasses.Gadget;
import mainPackage.modelClasses.GadgetTableItem;
import java.util.ArrayList;

public class GadgetsController implements DynamicFrame {

    @FXML
    private TableColumn<GadgetTableItem, String> clmType;
    @FXML
    private TableColumn<GadgetTableItem, String> clmRoom;
    @FXML
    private TableColumn<GadgetTableItem, String> clmName;
    @FXML
    private TableView<GadgetTableItem> tblViewCurrentGadgets;

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

    private ArrayList<GadgetTableItem> gadgetTableItemsList;


    public void initialize(){
        //making sure so the mainwindow knows which controller that is in charge.
        Main.getMainWindowController().setCurrentDynamicFrameController(this);

        gadgetTableItemsList = new ArrayList<>();
        updateFrame();
    }

    @Override
    public void updateFrame() {
        createGadgetTableItems();
        clmName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        clmType.setCellValueFactory(new PropertyValueFactory<>("type"));

        //This will be using a list that has itterated through the gadgetList in MainwindowController
        tblViewCurrentGadgets.getItems().addAll(gadgetTableItemsList);

    }

    private void createGadgetTableItems(){
        for (Gadget g : Main.getMainWindowController().gadgetList){
            GadgetTableItem gTI = new GadgetTableItem(g.getName(),g.getClass().getSimpleName(),g.getRoom(),g.getId());
            gadgetTableItemsList.add(gTI);
        }
    }

    public void onDeleteClicked(){

    }

    public void onUpdateClicked(){

    }

    public void onAddClicked(){

    }
}
