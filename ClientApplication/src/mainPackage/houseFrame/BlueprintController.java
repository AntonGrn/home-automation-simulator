package mainPackage.houseFrame;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import mainPackage.DynamicFrame;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import mainPackage.Main;
import mainPackage.modelClasses.Gadget;
import mainPackage.modelClasses.GuiObject;

import java.util.ArrayList;

public class BlueprintController implements DynamicFrame {

    @FXML
    private AnchorPane blueprint;

    @FXML
    private TableView<GuiObject> tblVKitchen;
    @FXML
    private TableColumn<GuiObject, ImageView> clmKitchen;

    @FXML
    private TableView<GuiObject> tblVLivingRoom;
    @FXML
    private TableColumn<GuiObject, ImageView> clmLivingroom;

    @FXML
    private TableView<GuiObject> tblVToilet;
    @FXML
    private TableColumn<GuiObject, ImageView> clmToilet;

    @FXML
    private TableView<GuiObject> tblVBedRoom;
    @FXML
    private TableColumn<GuiObject, ImageView> clmBedroom;

    @FXML
    private TableView<GuiObject> tblVGarage;
    @FXML
    private TableColumn<GuiObject, ImageView> clmGarage;


    ArrayList<GuiObject> kitchenGadgetList = new ArrayList<>();
    ArrayList<GuiObject> livingroomGadgetList = new ArrayList<>();
    ArrayList<GuiObject> toiletGadgetList = new ArrayList<>();
    ArrayList<GuiObject> bedroomGadgetList = new ArrayList<>();
    ArrayList<GuiObject> garageGadgetList = new ArrayList<>();

    public void initialize() {
        clmKitchen.setCellValueFactory(new PropertyValueFactory<>("typeOfGadget"));
        clmLivingroom.setCellValueFactory(new PropertyValueFactory<>("typeOfGadget"));
        clmToilet.setCellValueFactory(new PropertyValueFactory<>("typeOfGadget"));
        clmGarage.setCellValueFactory(new PropertyValueFactory<>("typeOfGadget"));
        clmBedroom.setCellValueFactory(new PropertyValueFactory<>("typeOfGadget"));

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
        showGadgetOnBlueprint();
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

    public void showGadgetOnBlueprint() {
        String typeOfGadget;
        try {
            for (Gadget g : Main.getMainWindowController().gadgetList) {
                switch (g.getRoom()) {
                    case "Kitchen":
                        typeOfGadget = g.getClass().getSimpleName() + g.getState();
                        GuiObject kitchenGuiObject = new GuiObject(typeOfGadget);
                        kitchenGadgetList.add(kitchenGuiObject);
                        break;
                    case "Livingroom":
                        typeOfGadget = g.getClass().getSimpleName() + g.getState();
                        GuiObject livingroomGuiObject = new GuiObject(typeOfGadget);
                        livingroomGadgetList.add(livingroomGuiObject);
                        break;
                    case "Toilet":
                        typeOfGadget = g.getClass().getSimpleName() + g.getState();
                        GuiObject toiletGuiObject = new GuiObject(typeOfGadget);
                        toiletGadgetList.add(toiletGuiObject);
                        break;
                    case "Garage":
                        typeOfGadget = g.getClass().getSimpleName() + g.getState();
                        GuiObject garageGuiObject = new GuiObject(typeOfGadget);
                        garageGadgetList.add(garageGuiObject);
                        break;
                    case "Bedroom":
                        typeOfGadget = g.getClass().getSimpleName() + g.getState();
                        GuiObject bedroomGuiObject = new GuiObject(typeOfGadget);
                        bedroomGadgetList.add(bedroomGuiObject);
                        break;
                }
            }
            tblVKitchen.getItems().clear();
            tblVKitchen.getItems().addAll(kitchenGadgetList);
            tblVLivingRoom.getItems().clear();
            tblVLivingRoom.getItems().addAll(livingroomGadgetList);
            tblVToilet.getItems().clear();
            tblVToilet.getItems().addAll(toiletGadgetList);
            tblVGarage.getItems().clear();
            tblVGarage.getItems().addAll(garageGadgetList);
            tblVBedRoom.getItems().clear();
            tblVBedRoom.getItems().addAll(bedroomGadgetList);
        } catch (Exception e) {
            e.printStackTrace();
            Main.getMainWindowController().exceptionLabel.setText("Could not load gadgets into blueprint");
        }
    }
}
