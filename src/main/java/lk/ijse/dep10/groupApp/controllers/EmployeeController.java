package lk.ijse.dep10.groupApp.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import lk.ijse.dep10.groupApp.db.DBConnection ;
import lk.ijse.dep10.groupApp.models.Employee;


import java.sql.*;

public class EmployeeController {
    public Button btnNew;
    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<Employee> tblEmployee;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtID;

    @FXML
    private TextField txtName;

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        try {
            Employee emp = tblEmployee.getSelectionModel().getSelectedItem();
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preStm = connection.prepareStatement("delete from Employee where id =?");
            preStm.setString(1, emp.getId());
            preStm.execute();
            tblEmployee.getItems().remove(emp);

            tblEmployee.getSelectionModel().clearSelection();
            btnNew.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Fail to delete from database");
            throw new RuntimeException(e);
        }

    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        try {
            if (!validate()) return;
            String id = txtID.getText();
            String name = txtName.getText();
            String address = txtAddress.getText();

            Connection connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            if (tblEmployee.getSelectionModel().getSelectedItem() == null) {
                PreparedStatement preStm = connection.prepareStatement("Insert into Employee value (?,?,?)");

                preStm.setString(1, id);
                preStm.setString(2, name);
                preStm.setString(3, address);
                preStm.execute();

                Employee employee = new Employee(id, name, address);
                tblEmployee.getItems().add(employee);

            } else if (tblEmployee.getSelectionModel().getSelectedItem() != null) {

                Employee emp = tblEmployee.getSelectionModel().getSelectedItem();
                PreparedStatement preStm2 = connection.prepareStatement("update  Employee set name =?,address=? where id =?");
                preStm2.setString(1, txtName.getText());
                preStm2.setString(2, txtAddress.getText());
                preStm2.setString(3, emp.getId());
                preStm2.execute();

                Employee empNew = new Employee(txtID.getText(), txtName.getText(), txtAddress.getText());
                tblEmployee.getItems().clear();
                updateTable();


            }
            connection.commit();
            btnNew.fire();


        } catch (Throwable e) {
            e.printStackTrace();
            try {
                DBConnection.getInstance().getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }


    }

    public void initialize() {
        btnDelete.setDisable(true);
        txtID.setDisable(true);
        updateTable();
        generateId();


        tblEmployee.getSelectionModel().selectedItemProperty().addListener((value, previous, current) -> {
            btnDelete.setDisable(current == null);
            if (current != null) {
                txtID.setText(current.getId());
                txtName.setText(current.getName());
                txtAddress.setText(current.getAddress());
            }

        });
        tblEmployee.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblEmployee.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblEmployee.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    public boolean validate() {
        boolean validate = true;
        if (!txtAddress.getText().matches(".{5,}")) {
            txtAddress.requestFocus();
            txtAddress.getStyleClass().add("invalid");
            validate = false;

        }
        if (!txtName.getText().strip().matches("[A-z ]{5,}")) {
            txtName.requestFocus();
            txtName.getStyleClass().add("invalid");
            validate = false;
        }
        return validate;
    }

    private void generateId() {

        if (tblEmployee.getItems().isEmpty()) {
            txtID.setText("E001");
        } else {
            ObservableList<Employee> empList = tblEmployee.getItems();
            txtID.setText(String.format("E%03d", Integer.parseInt(empList.get(empList.size() - 1).getId().substring(1)) + 1));
        }

    }

    private void updateTable() {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Employee");
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                String address = resultSet.getString(3);
                Employee employee = new Employee(id, name, address);
                tblEmployee.getItems().add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public void btnNewOnAction(ActionEvent actionEvent) {
        txtName.clear();
        txtAddress.clear();
        generateId();
        tblEmployee.getSelectionModel().clearSelection();
    }
}
