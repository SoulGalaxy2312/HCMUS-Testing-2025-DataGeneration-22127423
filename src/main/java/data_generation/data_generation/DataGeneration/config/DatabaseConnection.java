package data_generation.data_generation.DataGeneration.config;

import java.sql.*;

import org.springframework.stereotype.Component;

@Component
public class DatabaseConnection {

    private String DB_HOST = "localhost";
    private String DB_PORT = "3306";
    private String DB_NAME = "toolshop";

    private String DB_USER = "root";
    private String DB_PASSWORD = "root";

    public Connection connect() throws SQLException {
        String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }
}