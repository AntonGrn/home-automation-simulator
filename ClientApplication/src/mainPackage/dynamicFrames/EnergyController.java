package mainPackage.dynamicFrames;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import mainPackage.DynamicFrame;
import mainPackage.Main;
import mainPackage.modelClasses.Gadget;

public class EnergyController implements DynamicFrame {

    @FXML
    private Label lblKitchen;

    @FXML
    private GridPane gridPaneRoot;

    @FXML
    private Label lblToilet;

    @FXML
    private Label lblCurrentWattage;

    @FXML
    private Label lblGarage;

    @FXML
    private Label lblLivingroom;

    @FXML
    private ProgressIndicator progressindicator;

    @FXML
    private Label lblBedroom;

    private Double totalConsumption = 0.0;
    private Double currentConsumption = 0.0;


    public void initialize() {
        updateFrame();
    }

    @Override
    public void updateFrame() {
        calculateConsumptionOverView();
    }


    public void calculateConsumptionOverView() {
        Double bedRoomConsumption = calculateSingleRoomConsumption("Bedroom");
        Double kitchenConsumption = calculateSingleRoomConsumption("Kitchen");
        Double toiletConsumption = calculateSingleRoomConsumption("Toilet");
        Double livingRoomConsumption = calculateSingleRoomConsumption("Livingroom");
        Double garageConsumption = calculateSingleRoomConsumption("Garage");

        for (Gadget g : Main.getMainWindowController().gadgetList) {
            totalConsumption = totalConsumption + g.getConsumption();
        }


        lblBedroom.setText(String.format("%s%.3f%s" ,"Bedroom: " , ((bedRoomConsumption / totalConsumption) * 100) , " %"));
        lblLivingroom.setText(String.format("%s%.3f%s" , "Livingroom: " , ((livingRoomConsumption / totalConsumption) * 100) , " %"));
        lblKitchen.setText(String.format("%s%.3f%s" , "Kitchen: " , ((kitchenConsumption / totalConsumption) * 100), " %"));
        lblToilet.setText(String.format("%s%.3f%s" , "Toilet: " , ((toiletConsumption / totalConsumption) * 100) , " %"));
        lblGarage.setText(String.format("%s%.3f%s" , "Garage: " , ((garageConsumption / totalConsumption) * 100) , " %"));
        lblCurrentWattage.setText(String.format("%s%.3f%s" ,"Currently used percent out of " , totalConsumption , " W"));


        Double progress = currentConsumption / totalConsumption;
        progressindicator.setProgress(progress);
    }

    public void replacePromptText(){
        progressindicator.applyCss();
        Text text = (Text)progressindicator.lookup(".text.percentage");
        text.setText("");
    }

    public Double calculateSingleRoomConsumption(String room) {
        Double consumption = 0.0;
        for (Gadget g : Main.getMainWindowController().gadgetList) {
            if (g.getRoom().equals(room) && (g.getState().equals(true) || g.getState().toString().contains("2"))) {
                consumption = consumption + g.getConsumption();
                currentConsumption = currentConsumption + g.getConsumption();
            }
        }
        System.out.println("Currentconsumtion: " + currentConsumption);
        return consumption;
    }
}
