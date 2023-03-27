package lk.ijse.dep10.groupApp.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.dep10.groupApp.db.DBConnection;
import lk.ijse.dep10.teacher.model.Teacher;

import java.sql.*;
import java.util.ArrayList;

public class TeacherController {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnNewTeacher;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<Teacher> tblTeachers;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;
    private Connection connection;

    public void initialize(){
        btnDelete.setDisable(true);

        tblTeachers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblTeachers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblTeachers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        loadAllTeachers();

        tblTeachers.getSelectionModel().selectedItemProperty().addListener(((ov, previous, current) ->{
            btnDelete.setDisable(current==null);
        } ));


    }

    private void loadAllTeachers() {
        try {
            connection= DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rstTeacher = stm.executeQuery("SELECT * FROM Teachers");

            while (rstTeacher.next()){
                int id = rstTeacher.getInt("id");
                String name = rstTeacher.getString("name");
                String address = rstTeacher.getString("address");

                Teacher teacher=new Teacher(id,name,address);
                tblTeachers.getItems().add(teacher);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        try{
            connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            String sql = "DELETE FROM Teachers WHERE id=%d";
            sql = String.format(sql, tblTeachers.getSelectionModel().getSelectedItem().getId());
            stm.executeUpdate(sql);

            tblTeachers.getItems().remove(tblTeachers.getSelectionModel().getSelectedItem());
            if (tblTeachers.getItems().isEmpty()) btnNewTeacher.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the teacher, try again!").show();
        }


    }

    @FXML
    void btnNewTeacherOnAction(ActionEvent event) {
        ObservableList<Teacher> studentList = tblTeachers.getItems();
        int newId = (studentList.isEmpty() ? 1 : studentList.get(studentList.size() - 1).getId() + 1);
        txtId.setText(newId+ "");
//        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtName.requestFocus();
        tblTeachers.getSelectionModel().clearSelection();
//        txtName.getStyleClass().remove("invalid");
//        txtAddress.getStyleClass().remove("invalid");

    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if(!dataValid()) return;
        try {
            Teacher teacher = new Teacher(Integer.parseInt(txtId.getText()),txtName.getText(), txtAddress.getText());

            connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            Teacher slectedTeacher = tblTeachers.getSelectionModel().getSelectedItem();

            if (slectedTeacher == null) {
                String sql = "INSERT INTO Teachers VALUES (%d,'%s', '%s')";
                sql = String.format(sql, teacher.getId(),teacher.getName(), teacher.getAddress());
                stm.executeUpdate(sql);

                tblTeachers.getItems().add(teacher);
            } else {
                String sql = "UPDATE Teachers SET name='%s', address='%s' WHERE id=%d";
                sql = String.format(sql, teacher.getName(), teacher.getAddress());
                stm.executeUpdate(sql);

                ObservableList<Teacher> teachersList = tblTeachers.getItems();
                int selectedStudentIndex = teachersList.indexOf(slectedTeacher);
                teachersList.set(selectedStudentIndex, teacher);
                tblTeachers.refresh();
            }

            btnNewTeacher.fire();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the teacher, try again!").showAndWait();
        }


    }

    private boolean dataValid() {

        boolean isDataValid=true;
        txtName.getStyleClass().remove("invalid");
        txtAddress.getStyleClass().remove("invalid");
        String name=txtName.getText();
        String address=txtAddress.getText();

        if(!(address.matches("[a-zA-Z0-9 ]") )|| address.strip().length()<3){
            txtAddress.requestFocus();
            txtAddress.selectAll();
            txtAddress.getStyleClass().add("invalid");
        }

        if (!name.matches("[A-Za-z ]+")){
            txtName.requestFocus();
            txtName.selectAll();
            txtName.getStyleClass().add("invalid");
            isDataValid = false;
        }


        return isDataValid;
    }

    public void tblTeacherOnKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE) btnDelete.fire();
    }


}
