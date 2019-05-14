package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mainPackage.DynamicFrame;
import mainPackage.Main;
import mainPackage.modelClasses.Gadget;
import mainPackage.modelClasses.GadgetTableItem;

import java.io.IOException;
import java.util.ArrayList;

public class GadgetsController implements DynamicFrame {

    @FXML
    private ChoiceBox<String> dropMenuGadgets;

    @FXML
    private ChoiceBox<String> dropMenuRooms;

    @FXML
    private Button btnAddGadget, btnSubmit, btnDelete, btnEditGadget;

    @FXML
    private TableColumn<GadgetTableItem, String> clmType;

    @FXML
    private TableColumn<GadgetTableItem, String> clmRoom;

    @FXML
    private TableColumn<GadgetTableItem, String> clmName;

    @FXML
    private TextField txtfldConsumption, txtfldGadgetName;

    @FXML
    private TableView<GadgetTableItem> tblViewCurrentGadgets;

    private ArrayList<GadgetTableItem> gadgetTableItemsList;


    public void initialize() {
        //Adding all rooms to choiseBoxRooms
        String bedRoom = "Bedroom";
        String livingRoom = "Livingroom";
        String toilet = "Toilet";
        String kitchen = "Kitchen";
        String garage = "Garage";
        dropMenuRooms.getItems().addAll(bedRoom, livingRoom, toilet, kitchen, garage);
        dropMenuRooms.getSelectionModel().selectFirst();//Making bedroom as selected item from beginning

        //Adding gadgets to choiseBoxGadgets
        String lamp = "Lamp";
        String door = "Door";
        String multiMedia = "Multimedia";
        String heat = "Heat";
        dropMenuGadgets.getItems().addAll(lamp, door, multiMedia, heat);
        dropMenuGadgets.getSelectionModel().selectFirst(); //Making lamp as selected item from beginning

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

    private void createGadgetTableItems() {
        for (Gadget g : Main.getMainWindowController().gadgetList) {
            GadgetTableItem gTI = new GadgetTableItem(g.getName(), g.getClass().getSimpleName(), g.getRoom(), g.getId());
            gadgetTableItemsList.add(gTI);
        }
    }

    public void onDeleteGadgetClicked() {
        //Makes sure selected row is not empty
        if (tblViewCurrentGadgets.getSelectionModel().getSelectedItem() != null) {
            GadgetTableItem gadgetItem = tblViewCurrentGadgets.getSelectionModel().getSelectedItem();

            //Loops through gadgetList in main too find the correct gadget (compares gadget id)
            for (Gadget g : Main.getMainWindowController().gadgetList) {
                if (g.getId() == gadgetItem.getId()) {
                /*try {
                    send request to server for deletion of gadget
                }catch (IOException io){
                    Main.getMainWindowController().exceptionLabel.setText("Could not delete gadget..");
                }*/
                } else {
                    Main.getMainWindowController().exceptionLabel.setText("Something went wrong..");
                }
            }
        }
    }

    public void onAddGadgetClicked() {

    }

    public void onSubmitClicked() {

    }

    public void onEditGadgetClicked() {

    }
}
