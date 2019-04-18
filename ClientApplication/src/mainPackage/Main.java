package mainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static MainWindowController mainWindowController;  //We set this as instance, so we can reach it later on.

    public static MainWindowController getMainWindowController(){  //Our way of accessing our one-and-only Controller object
        return mainWindowController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = (Parent)loader.load();
        mainWindowController = (MainWindowController) loader.getController();

        primaryStage.setScene(new Scene(root));
        // For setting icon: primaryStage.getIcons().add(new Image("IMAGE URL"));
        primaryStage.setTitle("LAAS");

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

