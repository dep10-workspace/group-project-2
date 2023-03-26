package lk.ijse.dep10.groupApp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class DashBoardController {

    @FXML
    private Button btnCustomer;

    @FXML
    private Button btnEmployee;

    @FXML
    private Button btnStudent;

    @FXML
    private Button btnTeacher;

    @FXML
    void btnCustomerOnAction(ActionEvent event) {

    }

    @FXML
    void btnEmployeeOnAction(ActionEvent event) {

    }

    @FXML
    void btnStudentOnAction(ActionEvent event) {
        Stage stage = (Stage) btnStudent.getScene().getWindow();
        try {
            stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/views/StudentView.fxml")).load()));
            stage.centerOnScreen();
            stage.setTitle("Manage Students");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to load Student view.. please try again").showAndWait();
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnTeacherOnAction(ActionEvent event) {

    }

}
