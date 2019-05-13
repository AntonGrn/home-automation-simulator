package mainPackage.dynamicFrames;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import mainPackage.DynamicFrame;
import mainPackage.Main;
import mainPackage.MainWindowController;
import mainPackage.ServerConnection;
import mainPackage.modelClasses.Gadget;
import mainPackage.modelClasses.GuiObject;
import mainPackage.modelClasses.RoomSlider;

import java.util.ArrayList;
import java.util.Set;

public class RoomsController implements DynamicFrame {

    @FXML
    private TableColumn<RoomSlider, Button> clmFive;
    @FXML
    private TableColumn<RoomSlider, Button> clmOne;
    @FXML
    private TableColumn<RoomSlider, Button> clmTwo;
    @FXML
    private TableColumn<RoomSlider, Button> clmThree;
    @FXML
    private TableColumn<RoomSlider, Button> clmFour;
    @FXML
    private TableColumn<GuiObject, ImageView> clmType;
    @FXML
    private TableColumn<GuiObject, String> clmId;
    @FXML
    private TableColumn<GuiObject, ImageView> clmState;
    @FXML
    private Button btnLeftHover, btnRightHover;

    @FXML
    private TableView<RoomSlider> tblViewRooms;
    @FXML
    private TableView<GuiObject> tblViewDynamicGadgets;

    @FXML
    private Pane dynamicFrameRooms;

    private ObservableList<RoomSlider> listOfRoomButtonsHeader;
    private Timeline timeLineLeft;
    private Timeline timeLineRight;
    private String currentRoomButtonSelected;

    public void initialize() {
        //making sure so the mainwindow knows which controller that is in charge.
        Main.getMainWindowController().setCurrentDynamicFrameController(this);

        //Make so only one cell is selected at once in tblview
        tblViewDynamicGadgets.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblViewDynamicGadgets.getSelectionModel().setCellSelectionEnabled(true);

        listOfRoomButtonsHeader = FXCollections.observableArrayList(RoomSlider.getRoomSliderInstance());

        //update all clients and tables and such, when a request is confirmed from server.
        updateFrame();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                RoomSlider.getRoomSliderInstance().getBedRoom().setOnAction(e -> {
                    String roomName = ((Button) e.getSource()).getUserData().toString();
                    updateTableView(roomName);
                    //show bedroom text on blueprint
                    Main.getMainWindowController().chosenRoom.setValue("bedRoom");
                });
                RoomSlider.getRoomSliderInstance().getKitchen().setOnAction(e -> {
                    String roomName = ((Button) e.getSource()).getUserData().toString();
                    updateTableView(roomName);
                    //show kitchen text on blueprint
                    Main.getMainWindowController().chosenRoom.setValue("kitchen");
                });
                RoomSlider.getRoomSliderInstance().getGarage().setOnAction(e -> {
                    String roomName = ((Button) e.getSource()).getUserData().toString();
                    updateTableView(roomName);
                    //show garage text on blueprint
                    Main.getMainWindowController().chosenRoom.setValue("garage");
                });
                RoomSlider.getRoomSliderInstance().getToilet().setOnAction(e -> {
                    String roomName = ((Button) e.getSource()).getUserData().toString();
                    updateTableView(roomName);
                    //show toilet text on blueprint
                    Main.getMainWindowController().chosenRoom.setValue("toilet");
                });
                RoomSlider.getRoomSliderInstance().getLivingRoom().setOnAction(e -> {
                    String roomName = ((Button) e.getSource()).getUserData().toString();
                    updateTableView(roomName);
                    //show livingroom text on blueprint
                    Main.getMainWindowController().chosenRoom.setValue("livingRoom");
                });
            }
        });
    }

    @Override
    public void updateFrame() {
        //Setting properties for RoomSlider in tableview.
        clmOne.setCellValueFactory(new PropertyValueFactory<>("bedRoom"));
        clmTwo.setCellValueFactory(new PropertyValueFactory<>("kitchen"));
        clmThree.setCellValueFactory(new PropertyValueFactory<>("garage"));
        clmFour.setCellValueFactory(new PropertyValueFactory<>("toilet"));
        clmFive.setCellValueFactory(new PropertyValueFactory<>("livingRoom"));

        //Setting properties for Gadgets in tableview.
        clmType.setCellValueFactory(new PropertyValueFactory<>("typeOfGadget"));
        clmId.setCellValueFactory(new PropertyValueFactory<>("gadgetName"));
        clmState.setCellValueFactory(new PropertyValueFactory<>("stateOfGadget"));

        //Hiding buttons with the hoverEffect
        btnLeftHover.setOpacity(1);
        btnRightHover.setOpacity(1);

        //add to tblView roomsHeader, will never be changed
        tblViewRooms.getItems().addAll(listOfRoomButtonsHeader);

        /*This variable is important so the application knows which room it is going to reload when
        a request is sent back from server, for example if you have turned on a lamp in bedroom
        the tableview is going to reload bedroom with the gadget turned on. */
        updateTableView(currentRoomButtonSelected);
    }

    private void updateTableView(String roomName) {
        ArrayList<GuiObject> gadgetList = new ArrayList<>();
        currentRoomButtonSelected = roomName;
        try {
            for (Gadget g : Main.getMainWindowController().gadgetList) {
                if (g.getRoom().equals(roomName)) {
                    String stateOfGadget;
                    String gadgetName = "";
                    String typeOfGadget = "";
                    if (g.getState() instanceof Boolean) {
                        if (g.getState().equals(true)) {
                            stateOfGadget = "switchButtonOn";
                        } else {
                            stateOfGadget = "switchButtonOff";
                        }
                        gadgetName = g.getName(); // example 'Lamp One'
                        typeOfGadget = g.getClass().getSimpleName() + g.getState(); //example 'Lampfalse'
                    } else {
                        stateOfGadget = g.getClass().getSimpleName() + String.valueOf(g.getState()); //example 'Heat20'
                    }
                    GuiObject guiObject = new GuiObject(typeOfGadget, gadgetName, stateOfGadget, g.getId());
                    gadgetList.add(guiObject);
                }
            }
            tblViewDynamicGadgets.getItems().clear();
            tblViewDynamicGadgets.getItems().addAll(gadgetList);

        } catch (Exception ex) {
            ex.printStackTrace();
            Main.getMainWindowController().exceptionLabel.setText("Could not load gadgets..hmm");
        }
    }


    public void onChangeStateOfGadget() {
        TablePosition pos = (TablePosition) tblViewDynamicGadgets.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
        TableColumn tableColumn = pos.getTableColumn();
        GuiObject gui = tblViewDynamicGadgets.getItems().get(row);

        if (tableColumn.getCellObservableValue(gui).getValue() instanceof ImageView) {
            ImageView data = (ImageView) tableColumn.getCellObservableValue(gui).getValue();
            if (data.getImage().getUrl().contains("switchButton") || data.getImage().getUrl().contains("Heat2")) {

                for (Gadget g : Main.getMainWindowController().gadgetList) {
                    if (g.getId() == gui.getId() && g.getName() == gui.getGadgetName()) {

                        System.out.println("Hello");
                        String serverRequest;
                        String id = String.valueOf(g.getId());
                        boolean state;
                        int temp;

                        if (g.getState() instanceof Boolean) {
                            state = !(Boolean) g.getState();
                            //create a protocol string according to Laas protocol.
                            serverRequest = String.format("/s/s/s/s", "3:", id, ":", (state ? "1" : "0"));
                        } else {
                            //create a protocol string according to Laas protocol.
                            temp = (Integer) g.getState();
                            serverRequest = String.format("/s/s/s/s", "3:", id, ":", String.valueOf(temp));
                        }
                        try {
                            //add to request to server
                            Main.getMainWindowController().requestsToServer.put(serverRequest);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception ex) {
                            Main.getMainWindowController().exceptionLabel.setText("Could not change state of gadget");
                        }
                    }
                }
            }
        }
    }

    public void scrollLeft() {
        Set<Node> node = tblViewRooms.lookupAll(".scroll-bar");
        for (final Node nodeFound : node) {
            if (nodeFound instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) nodeFound;
                if (scrollBar.getOrientation() == Orientation.HORIZONTAL) {
                    timeLineLeft = new Timeline(new KeyFrame(Duration.millis(30), e -> {
                        if (scrollBar.getValue() >= scrollBar.getMin()) {
                            scrollBar.setValue(scrollBar.getValue() - 4);
                        }
                    }));
                    timeLineLeft.setCycleCount(Animation.INDEFINITE);
                    timeLineLeft.play();
                }
            }
        }
    }

    public void scrollRight() {
        Set<Node> node = tblViewRooms.lookupAll(".scroll-bar");
        for (final Node nodeFound : node) {
            if (nodeFound instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) nodeFound;
                if (scrollBar.getOrientation() == Orientation.HORIZONTAL) {
                    timeLineRight = new Timeline(new KeyFrame(Duration.millis(30), e -> {
                        if (scrollBar.getValue() <= scrollBar.getMax()) {
                            scrollBar.setValue(scrollBar.getValue() + 4);
                        }
                    }));
                    timeLineRight.setCycleCount(Animation.INDEFINITE);
                    timeLineRight.play();
                }
            }
        }
    }

    public void stopScrollLeft() {
        timeLineLeft.stop();
    }

    public void stopScrollRight() {
        timeLineRight.stop();
    }
}
