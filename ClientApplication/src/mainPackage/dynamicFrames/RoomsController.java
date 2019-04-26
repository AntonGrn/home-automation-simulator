package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import mainPackage.DynamicFrame;

public class RoomsController implements DynamicFrame {

    @FXML
    private TableColumn<?, ?> clmFive;
    @FXML
    private TableColumn<?, ?> clmOne;
    @FXML
    private TableColumn<?, ?> clmTwo;
    @FXML
    private TableColumn<?, ?> clmThree;
    @FXML
    private TableColumn<?, ?> clmFour;

    @FXML
    private TableView<?> tblViewRooms;

    @FXML
    private Pane dynamicFrameRooms;

    @FXML
    private Pane paneHeaderRooms;


    @Override
    public void updateFrame() {

    }
}
