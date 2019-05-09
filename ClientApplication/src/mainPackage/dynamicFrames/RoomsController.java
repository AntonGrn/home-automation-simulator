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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import mainPackage.DynamicFrame;
import mainPackage.Main;
import mainPackage.MainWindowController;
import mainPackage.modelClasses.Gadget;
import mainPackage.modelClasses.GuiObject;
import mainPackage.modelClasses.Room;
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

        //Later on we will set the btnLeftHover and btnRightHover opacity to 0 so they are not visible.

        listOfRoomButtonsHeader = FXCollections.observableArrayList(RoomSlider.getRoomSliderInstance());

        //update all clients and tables and such, when a request is confirmed from server.
        updateFrame();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                RoomSlider.getRoomSliderInstance().getBedRoom().setOnAction(e -> {
                    String roomName = ((Button) e.getSource()).getUserData().toString();
                    updateTableView(roomName);
                });
                RoomSlider.getRoomSliderInstance().getKitchen().setOnAction(e -> {
                    String roomName = ((Button) e.getSource()).getUserData().toString();
                    updateTableView(roomName);
                });
                RoomSlider.getRoomSliderInstance().getGarage().setOnAction(e -> {
                    String roomName = ((Button) e.getSource()).getUserData().toString();
                    updateTableView(roomName);
                });
                RoomSlider.getRoomSliderInstance().getToilet().setOnAction(e -> {
                    String roomName = ((Button) e.getSource()).getUserData().toString();
                    updateTableView(roomName);
                });
                RoomSlider.getRoomSliderInstance().getLivingRoom().setOnAction(e -> {
                    String roomName = ((Button) e.getSource()).getUserData().toString();
                    updateTableView(roomName);
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

        /*from beginning the tableview will not have any items inside it,
        when a room button is pressed then it will go through gadgetList in MainWindow
        and add all rooms with the same name as button. */

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
                    GuiObject guiObject = new GuiObject(typeOfGadget,gadgetName,stateOfGadget);
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
