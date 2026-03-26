package com.urbanissue.db;

/**
 * Central database configuration. Change these for your MySQL setup.
 */
public final class DBConfig {
    public static final String URL = "jdbc:mysql://localhost:3306/civictrack?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "password";

    private DBConfig() {}
}
