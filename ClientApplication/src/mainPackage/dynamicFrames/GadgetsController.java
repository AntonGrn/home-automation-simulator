package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mainPackage.DynamicFrame;
import mainPackage.Main;
import mainPackage.modelClasses.Gadget;
import mainPackage.modelClasses.GadgetTableItem;
import mainPackage.modelClasses.Lamp;

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

                    String type = g.getClass().getSimpleName();
                    String id = String.valueOf(g.getId());
                    String keepOrRemove = "remove";
                    String name = g.getName();
                    String room = g.getRoom();
                    String state = String.valueOf(g.getState());
                    String consumption = String.valueOf(g.getConsumption());

                    //Creating Laas string according to protocol.
                    String serverReguest = String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s", "5:", type, ":", id, ":", keepOrRemove, ":", name, ":", room, ":", state, ":", consumption);

                            /*try {
                                Main.getMainWindowController().requestsToServer.put(serverReguest);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            } catch (Exception e) {
                                Main.getMainWindowController().exceptionLabel.setText("Could not delete gadget..");
                            }*/
                } else {
                    Main.getMainWindowController().exceptionLabel.setText("Something went wrong..");
                }
            }
        } else {
            Main.getMainWindowController().exceptionLabel.setText("Please select a valid row in table..");
        }
    }

    public void onAddGadgetClicked() {
        //Sets user data according to action requested
        btnSubmit.setUserData("addGadget");

        //enabling txtfields and buttons and dropdownmenus
        txtfldConsumption.setDisable(false);
        txtfldGadgetName.setDisable(false);
        dropMenuGadgets.setDisable(false);
        dropMenuRooms.setDisable(false);
        btnSubmit.setDisable(false);
    }

    public void onEditGadgetClicked() {
        //Sets user data according to action requested
        btnSubmit.setUserData("editGadget");

        //enabling txtfields and buttons and dropdownmenus
        txtfldConsumption.setDisable(false);
        txtfldGadgetName.setDisable(false);
        dropMenuGadgets.setDisable(false);
        dropMenuRooms.setDisable(false);
        btnSubmit.setDisable(false);
    }

    public void onSubmitClicked() {
        //Actions depends on the userData
        switch (btnSubmit.getUserData().toString()) {
            case "editGadget":
                //Makes sure selected row is not empty
                if (tblViewCurrentGadgets.getSelectionModel().getSelectedItem() != null) {
                    GadgetTableItem gadgetItem = tblViewCurrentGadgets.getSelectionModel().getSelectedItem();

                    //Loops through gadgetList in main too find the correct gadget (compares gadget id)
                    for (Gadget g : Main.getMainWindowController().gadgetList) {
                        if (g.getId() == gadgetItem.getId()) {
                            //Make request to server for update of gadget (either change of consumption or change of name)

                            String type = g.getClass().getSimpleName();
                            String newName = txtfldGadgetName.getText();
                            String newConsumption = txtfldConsumption.getText();
                            String id = String.valueOf(g.getId());
                            String keepOrRemove = "keep";
                            String state = String.valueOf(g.getState());
                            String room = g.getRoom();

                            //Creating Laas string according to protocol.
                            String serverRequest = String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s", "5:", type, ":", id, ":", keepOrRemove, ":", newName, ":", room, ":", state, ":", newConsumption);

                            /*try {
                                //putting serverRequest into requestToServer list.
                                Main.getMainWindowController().requestsToServer.put(serverRequest);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                Main.getMainWindowController().exceptionLabel.setText("Could not update gadget info..");
                            }*/
                        }
                    }
                } else {
                    Main.getMainWindowController().exceptionLabel.setText("Please select a valid row in table..");
                }
                break;
            case "addGadget":
                //Makes sure so a room and gadget is selected for insertion to server
                if (dropMenuGadgets.getSelectionModel().getSelectedItem() != null ||
                        dropMenuRooms.getSelectionModel().getSelectedItem() != null) {

                    String regex = "\\d+"; //0-9 on or more times
                    //Makes sure the textfields not are empty
                    if (!txtfldConsumption.getText().trim().isEmpty() &&
                            !txtfldGadgetName.getText().trim().isEmpty()) {

                        if (txtfldConsumption.getText().trim().matches(regex)) {
                            String room = dropMenuRooms.getSelectionModel().getSelectedItem();
                            String gadget = dropMenuGadgets.getSelectionModel().getSelectedItem();
                            String nameOfGadget = txtfldGadgetName.getText();
                            String consumption = txtfldConsumption.getText();

                            //Creating Laas string according to protocol.
                            String serverRequest = String.format("%s%s%s%s%s%s%s%s", "6:", gadget, ":", nameOfGadget, ":", room, ":", consumption);

                            /*try {
                                //putting serverRequest into requestToServer list.
                                Main.getMainWindowController().requestsToServer.put(serverRequest);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            } catch (Exception e) {
                                Main.getMainWindowController().exceptionLabel.setText("Could not add gadget..");
                            }*/
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

        //setting all buttons,textfields and dropmenus as disabled so user have to click for example "add gadget"
        txtfldConsumption.setDisable(true);
        txtfldGadgetName.setDisable(true);
        dropMenuGadgets.setDisable(true);
        dropMenuRooms.setDisable(true);
        btnSubmit.setDisable(true);
    }
}
