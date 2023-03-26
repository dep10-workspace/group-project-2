package lk.ijse.dep10.groupApp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.dep10.groupApp.db.DBConnection;
import lk.ijse.dep10.groupApp.models.Student;

import java.sql.*;
import java.util.Optional;

public class StudentController {

    @FXML
    private Button btnSave;

    @FXML
    private Button btnAddNewStudent;

    @FXML
    private Button btnDelete;

    @FXML
    private TableView<Student> tblStudents;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    public void initialize() {
        tblStudents.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblStudents.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblStudents.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        loadStudents();
        txtName.textProperty().addListener((observableValue, s, current) ->{
            txtName.getStyleClass().remove("invalid");
            if (!current.matches("[A-Za-z ]{3,}")) {
                txtName.getStyleClass().add("invalid");
            }
        } );
        txtAddress.textProperty().addListener((observableValue, s, current) ->{
            txtAddress.getStyleClass().remove("invalid");
            if (!current.matches("[A-Za-z ]{3,}")) {
                txtAddress.getStyleClass().add("invalid");
            }
        } );
        tblStudents.getSelectionModel().selectedItemProperty().addListener((observableValue, student, current) ->{
            if (current == null) {
                btnDelete.setDisable(true);
                return;
            }
            txtId.setText(current.getId());
            txtName.setText(current.getName());
            txtAddress.setText(current.getAddress());
            btnDelete.setDisable(false);
        } );
    }

    private void loadStudents() {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT *FROM Students");
            while (rst.next()) {
                String id = rst.getString(1);
                String name = rst.getString(2);
                String address = rst.getString(3);
                Student student = new Student(id, name, address);
                tblStudents.getItems().add(student);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Fail to load data from the data base please try again").showAndWait();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnAddNewStudentOnAction(ActionEvent event) {
        for (Node node:new Node[]{txtAddress,btnSave,txtName}) {
            node.setDisable(false);
            if (node instanceof TextField) {
                ((TextField) node).clear();
                node.getStyleClass().remove("invalid");
                node.requestFocus();
            }
        }
        studentIdGenerate();

    }

    private void studentIdGenerate() {
        int id = tblStudents.getItems().isEmpty() ? 1 : Integer.parseInt(tblStudents.getItems().get(tblStudents.getItems().size() - 1).getId().substring(1))+1;
        txtId.setText(String.format("S%03d", id));
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        if (tblStudents.getItems().isEmpty()) {
            btnAddNewStudent.fire();
            return;
        }
        String id = tblStudents.getSelectionModel().getSelectedItem().getId();

        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            String sql = "DELETE FROM Students WHERE id='%s'";
            sql= String.format(sql, id);
            stm.executeUpdate(sql);
            tblStudents.getItems().remove(tblStudents.getSelectionModel().getSelectedItem());
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Fail to delete the Student please try again").showAndWait();
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String name = txtName.getText().strip();
        String address = txtAddress.getText().strip();
        String id = txtId.getText().strip();
        if (!isDataValid(name,address))return;
        Student student = new Student(id, name, address);
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Students(id, name, address) VALUES (?,?,?)");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, address);
            preparedStatement.execute();
            tblStudents.getItems().add(student);
            btnAddNewStudent.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to save the student Please try again").showAndWait();
            throw new RuntimeException(e);
        }
    }

    private boolean isDataValid(String name,String address) {
        boolean dataValid=true;
        if (address.isEmpty() || txtAddress.getStyleClass().contains("invalid")) {
            txtAddress.selectAll();
            txtAddress.requestFocus();
            if(!txtAddress.getStyleClass().contains("invalid")) txtAddress.getStyleClass().add("invalid");
            dataValid=false;
        }
        if (name.isEmpty() || txtName.getStyleClass().contains("invalid")) {
            txtName.selectAll();
            txtName.requestFocus();
            if(!txtName.getStyleClass().contains("invalid")) txtName.getStyleClass().add("invalid");
            dataValid=false;
        }
        return dataValid;
    }

    @FXML
    private void tblStudentsOnKeyReleased(KeyEvent event) {
        if(event.getCode()== KeyCode.DELETE)btnDelete.fire();
    }

}
