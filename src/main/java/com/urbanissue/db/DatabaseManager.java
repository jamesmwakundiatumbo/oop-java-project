package com.urbanissue.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton for database connection management.
 * Demonstrates: Singleton pattern (single shared connection source).
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private final String url;
    private final String username;
    private final String password;

    private DatabaseManager() {
        this.url = DBConfig.URL;
        this.username = DBConfig.USERNAME;
        this.password = DBConfig.PASSWORD;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
