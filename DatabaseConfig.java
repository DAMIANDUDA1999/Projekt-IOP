package pl.logistics.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String IP = "192.168.4.113"; 
    private static final String DB_NAME = "logistics_db";
    private static final String USER = "root";
    private static final String PASS = "password123";
    private static final String URL = "jdbc:mysql://" + IP + ":3306/" + DB_NAME;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}