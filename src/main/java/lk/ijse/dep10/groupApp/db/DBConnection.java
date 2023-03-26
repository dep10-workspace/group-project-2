package lk.ijse.dep10.groupApp.db;

import com.mysql.cj.util.DnsSrv;
import javafx.scene.control.Alert;

import java.awt.dnd.DragGestureEvent;
import java.io.*;
import java.sql.*;
import java.util.Properties;

public class DBConnection {
    private static DBConnection dbConnection;
    private final Connection connection;

    private DBConnection() {
        try {
            File file = new File("application.properties");
            Properties properties = new Properties();
            try {
                properties.load(new FileReader(file));
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR,"OOPS...Properties file missing").showAndWait();
                throw new RuntimeException(e);
            }
            String host = properties.getProperty("dep10.host", "dep10.lk");
            String port = properties.getProperty("dep10.port", "3306");
            String database = properties.getProperty("dep10.database", "dep10_git");
            String user = properties.getProperty("dep10.user", "root");
            String password = properties.getProperty("dep10.password", "");
            String sql = String.format("jdbc:mysql://%s:%s/%s", host,port,database);
            connection= DriverManager.getConnection(sql,user,password);

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"failed to connect with database try again ").showAndWait();
            throw new RuntimeException(e);
        }
    }

    public static DBConnection getInstance() {
        return dbConnection == null ? dbConnection = new DBConnection() : dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}
