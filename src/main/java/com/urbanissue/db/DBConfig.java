package com.urbanissue.db;

/**
 * Central database configuration. Change these for your MySQL setup.
 */
public final class DBConfig {
    public static final String URL = System.getProperty(
            "app.db.url",
            envOrDefault("CIVICTRACK_DB_URL",
                    "jdbc:mysql://localhost:3306/civictrack?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true")
    );
    public static final String USERNAME = System.getProperty(
            "app.db.username",
            envOrDefault("CIVICTRACK_DB_USERNAME", "root")
    );
    public static final String PASSWORD = System.getProperty(
            "app.db.password",
            envOrDefault("CIVICTRACK_DB_PASSWORD", "password")
    );

    private DBConfig() {}

    private static String envOrDefault(String key, String fallback) {
        String v = System.getenv(key);
        return v == null || v.isBlank() ? fallback : v.trim();
    }
}
