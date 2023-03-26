package lk.ijse.dep10.groupApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lk.ijse.dep10.groupApp.db.DBConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                if (!connection.isClosed() && connection != null) {
                    System.out.println("Connection is going to close");
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }));
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            tableCreate();
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

    private void tableCreate() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SHOW TABLES ");
            HashSet<Object> tableNameList = new HashSet<>();
            while (rst.next()) {
                tableNameList.add(rst.getString(1));
            }
            boolean tableAllExist = tableNameList.contains(Set.of("Students,Customers,Teachers,Employees"));
            if (!tableAllExist) {
                System.out.println("Tables are about to auto generate through the scheme.sql");
                stm.execute(schemaRead());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to create tables...try again").showAndWait();
            throw new RuntimeException(e);
        }
    }

    private String schemaRead() {
        BufferedReader br = null;
        try {
            InputStream is = getClass().getResourceAsStream("/schema.sql");
            br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (line != null) {
                stringBuilder.append(line);
                line= br.readLine();
            }
        return String.valueOf(stringBuilder);
        } catch (FileNotFoundException e) {
            new Alert(Alert.AlertType.ERROR,"schema.sql file not found... please contact the technical team").showAndWait();
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,"schema.sql file is empty... please contact the technical team").showAndWait();
            throw new RuntimeException(e);
        }

    }
}
