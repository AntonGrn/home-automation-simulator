package mainPackage.dynamicFrames;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import mainPackage.DynamicFrame;
import mainPackage.modelClasses.Room;
import mainPackage.modelClasses.RoomSlider;

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
    private TableView<RoomSlider> tblViewRooms;

    private ObservableList<RoomSlider> tblViewObjects = FXCollections.observableArrayList(new RoomSlider());

    @FXML
    private Pane dynamicFrameRooms;

    @FXML
    private Pane paneHeaderRooms;

    public void initialize(){
        clmOne.setCellValueFactory(new PropertyValueFactory<>("bedRoom"));
        clmTwo.setCellValueFactory(new PropertyValueFactory<>("kitchen"));
        clmThree.setCellValueFactory(new PropertyValueFactory<>("garage"));
        clmFour.setCellValueFactory(new PropertyValueFactory<>("toilet"));
        clmFive.setCellValueFactory(new PropertyValueFactory<>("livingRoom"));

        tblViewRooms.getItems().addAll(tblViewObjects);
    }


    @Override
    public void updateFrame() {

    }
}
