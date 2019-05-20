package mainPackage.dynamicFrames;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
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
    private Label lblOverview;

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


    public void initialize(){
        updateFrame();

    }

    @Override
    public void updateFrame() {
        calculateConsumptionOverView();
    }



    public void calculateConsumptionOverView(){
        Double bedRoomConsumption = calculateSingleRoomConsumption("Bedroom");
        Double kitchenConsumption = calculateSingleRoomConsumption("Kitchen");
        Double toiletConsumption = calculateSingleRoomConsumption("Toilet");
        Double livingRoomConsumption = calculateSingleRoomConsumption("Livingroom");
        Double garageConsumption = calculateSingleRoomConsumption("Garage");

        for (Gadget g: Main.getMainWindowController().gadgetList){
            totalConsumption = totalConsumption + g.getConsumption();
        }

        System.out.println("TotalConsumption: " + totalConsumption);

        lblBedroom.setText("Bedroom: " + String.valueOf((bedRoomConsumption/totalConsumption)*100) + " %");
        lblLivingroom.setText("Livingroom: " + String.valueOf((livingRoomConsumption/totalConsumption)*100) + " %");
        lblKitchen.setText("Kitchen: " + String.valueOf((kitchenConsumption/totalConsumption)*100) + " %");
        lblToilet.setText("Toilet: " + String.valueOf((toiletConsumption/totalConsumption)*100) + " %");
        lblGarage.setText("Garage: " + String.valueOf((garageConsumption/totalConsumption)*100) + " %");

        progressindicator.setProgress(currentConsumption/totalConsumption);
    }

    public Double calculateSingleRoomConsumption(String room){
        Double consumption = 0.0;
        for (Gadget g: Main.getMainWindowController().gadgetList){
            if (g.getRoom().equals(room) && (g.getState().equals(true) || g.getState().toString().contains("2"))){
                consumption = consumption + g.getConsumption();
                currentConsumption = currentConsumption + g.getConsumption();
            }
        }
        System.out.println("Currentconsumtion: " + currentConsumption);
        return consumption;
    }
}
