package lk.ijse.dep10.groupApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setScene(new Scene(new FXMLLoader(getClass().getResource("/views/DashBoardView.fxml")).load()));
            primaryStage.centerOnScreen();
            primaryStage.setTitle("Main View");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Fail to load the application Main View...Please contact the technical team").showAndWait();
            throw new RuntimeException(e);
        }
    }
}
