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
    private ChoiceBox<String> dropMenuGadgets;

    @FXML
    private ChoiceBox<String> dropMenuRooms;

    @FXML
    private Button btnSubmit;

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
        //disabling textfields and buttons so user have to press for example "add gadget" for activation.
        txtfldConsumption.setDisable(true);
        txtfldGadgetName.setDisable(true);
        dropMenuGadgets.setDisable(true);
        dropMenuRooms.setDisable(true);
        btnSubmit.setDisable(true);

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

        //setting prompt text in the tableview
        tblViewCurrentGadgets.setPlaceholder(new Label("You have no gadgets available"));
        updateFrame();
    }

    @Override
    public void updateFrame() {
        clmName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clmRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        clmType.setCellValueFactory(new PropertyValueFactory<>("type"));

        //clears list so it is not duplicated..
        createGadgetTableItems();

        //This will be using a list that has looped through the gadgetList in MainwindowController
        tblViewCurrentGadgets.getItems().clear();
        tblViewCurrentGadgets.getItems().addAll(gadgetTableItemsList);

    }

    private void createGadgetTableItems() {
        gadgetTableItemsList.clear();
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
                    String id = String.valueOf(g.getId());

                    //Creating Laas string according to protocol.
                    String serverReguest = String.format("%s%s", "7b:", id);

                    try {
                        Main.getMainWindowController().requestsToServer.put(serverReguest);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    } catch (Exception e) {
                        Main.getMainWindowController().exceptionLabel.setText("Could not delete gadget..");
                    }
                }
            }
        } else {
            Main.getMainWindowController().exceptionLabel.setText("Please select a valid row in table..");
        }
    }

    public void onAddGadgetClicked() {
        //Sets user data according to action requested
        btnSubmit.setUserData("addGadget");

        //clearing textfields
        txtfldGadgetName.clear();
        txtfldConsumption.clear();

        //enabling txtfields and buttons and dropdownmenus
        txtfldConsumption.setDisable(false);
        txtfldGadgetName.setDisable(false);
        dropMenuGadgets.setDisable(false);
        dropMenuRooms.setDisable(false);
        btnSubmit.setDisable(false);
    }

    public void onEditGadgetClicked() {
        if (tblViewCurrentGadgets.getSelectionModel().getSelectedItem() != null) {
            //Sets user data according to action requested
            btnSubmit.setUserData("editGadget");

            //clearing textfields
            txtfldGadgetName.clear();
            txtfldConsumption.clear();


            GadgetTableItem gadgetTableItem = tblViewCurrentGadgets.getSelectionModel().getSelectedItem();
            for (Gadget g : Main.getMainWindowController().gadgetList) {
                if (g.getId() == gadgetTableItem.getId()) {
                    //adding info from tableview into textfields and dropDownMenus
                    txtfldConsumption.setText(String.valueOf(g.getConsumption()));
                    txtfldGadgetName.setText(g.getName());
                    dropMenuRooms.getSelectionModel().select(g.getRoom());
                    dropMenuGadgets.getSelectionModel().select(g.getClass().getSimpleName());
                    break;
                }
            }

            //enabling txtfields and buttons and dropdownmenus
            txtfldConsumption.setDisable(false);
            txtfldGadgetName.setDisable(false);
            dropMenuGadgets.setDisable(false);
            dropMenuRooms.setDisable(false);
            btnSubmit.setDisable(false);
        } else {
            Main.getMainWindowController().exceptionLabel.setText("Select a row with valid information in it..");
        }
    }

    public void onSubmitClicked() {
        //Actions depends on the userData
        switch (btnSubmit.getUserData().toString()) {
            case "editGadget":
                GadgetTableItem gadgetItem = tblViewCurrentGadgets.getSelectionModel().getSelectedItem();

                String regex = "\\d+"; //0-9 one or more times
                //Makes sure the textfields not are empty
                if (!txtfldConsumption.getText().trim().isEmpty() &&
                        !txtfldGadgetName.getText().trim().isEmpty()) {

                    //makes sure the input in consumption is correct..
                    if (txtfldConsumption.getText().matches(regex)) {

                        //Loops through gadgetList in main too find the correct gadget (compares gadget id)
                        for (Gadget g : Main.getMainWindowController().gadgetList) {
                            if (g.getId() == gadgetItem.getId()) {
                                //Make request to server for update of gadget (either change of consumption or change of name)

                                String type = dropMenuGadgets.getSelectionModel().getSelectedItem();
                                String id = String.valueOf(g.getId());
                                String newName = txtfldGadgetName.getText();
                                String room = dropMenuRooms.getSelectionModel().getSelectedItem();
                                String newConsumption = txtfldConsumption.getText();

                                if (newName.contains(":")) {
                                    Main.getMainWindowController().exceptionLabel.setText("Please don't use ':' in name of gadget");
                                } else {
                                    //Creating Laas string according to protocol.
                                    String serverRequest = String.format("%s%s%s%s%s%s%s%s%s%s", "6:", type, ":", id, ":", newName, ":", room, ":", newConsumption);
                                    try {
                                        //putting serverRequest into requestToServer list.
                                        Main.getMainWindowController().requestsToServer.put(serverRequest);

                                        //setting all buttons,textfields and dropmenus as disabled so user have to click for example "add gadget"
                                        //after the request has been sent and none exceptions has been caught
                                        txtfldConsumption.setDisable(true);
                                        txtfldGadgetName.setDisable(true);
                                        dropMenuGadgets.setDisable(true);
                                        dropMenuRooms.setDisable(true);
                                        btnSubmit.setDisable(true);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        Main.getMainWindowController().exceptionLabel.setText("Could not update gadget info..");
                                    }
                                }
                            }
                        }
                    } else {
                        Main.getMainWindowController().exceptionLabel.setText("Please enter a valid consumption number..");
                    }
                } else {
                    Main.getMainWindowController().exceptionLabel.setText("Please enter valid information in both fields..");
                }
                break;

            case "addGadget":
                //Makes sure so a room and gadget is selected for insertion to server
                if (dropMenuGadgets.getSelectionModel().getSelectedItem() != null ||
                        dropMenuRooms.getSelectionModel().getSelectedItem() != null) {

                    String reg = "\\d+"; //0-9 one or more times
                    //Makes sure the textfields not are empty
                    if (!txtfldConsumption.getText().trim().isEmpty() &&
                            !txtfldGadgetName.getText().trim().isEmpty()) {

                        if (txtfldConsumption.getText().trim().matches(reg)) {
                            String type = dropMenuGadgets.getSelectionModel().getSelectedItem();
                            String nameOfGadget = txtfldGadgetName.getText();
                            String room = dropMenuRooms.getSelectionModel().getSelectedItem();
                            String consumption = txtfldConsumption.getText();

                            if (nameOfGadget.contains(":")) {
                                Main.getMainWindowController().exceptionLabel.setText("Please don't use ':' in name of gadget");
                            } else {
                                //Creating Laas string according to protocol.
                                String serverRequest = String.format("%s%s%s%s%s%s%s%s", "7a:", type, ":", nameOfGadget, ":", room, ":", consumption);
                                try {
                                    //putting serverRequest into requestToServer list.
                                    Main.getMainWindowController().requestsToServer.put(serverRequest);

                                    //setting all buttons,textfields and dropmenus as disabled so user have to click for example "add gadget"
                                    txtfldConsumption.setDisable(true);
                                    txtfldGadgetName.setDisable(true);
                                    dropMenuGadgets.setDisable(true);
                                    dropMenuRooms.setDisable(true);
                                    btnSubmit.setDisable(true);
                                } catch (InterruptedException ie) {
                                    ie.printStackTrace();
                                } catch (Exception e) {
                                    Main.getMainWindowController().exceptionLabel.setText("Could not add gadget..");
                                }
                            }
                        } else {
                            Main.getMainWindowController().exceptionLabel.setText("Please enter a valid consumption number..");
                        }
                    } else {
                        Main.getMainWindowController().exceptionLabel.setText("Please enter valid information in both fields..");
                    }
                } else {
                    Main.getMainWindowController().exceptionLabel.setText("Room and gadget needs to be selected for insertion..");
                }
                break;
            default:
                Main.getMainWindowController().exceptionLabel.setText("Could not preform Action requested..");
        }
    }
}
