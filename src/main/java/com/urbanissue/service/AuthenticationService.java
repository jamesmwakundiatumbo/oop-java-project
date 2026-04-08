package com.urbanissue.service;

import com.urbanissue.dao.UserDAO;
import com.urbanissue.model.User;
import com.urbanissue.util.SessionManager;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Handles login and registration. Uses SessionManager to store current user.
 */
public class AuthenticationService {
    public static final String SUPER_ADMIN_EMAIL = "superadmin@civictrack.local";
    public static final String SUPER_ADMIN_PASSWORD = "superadmin123";
    private final UserDAO userDAO = new UserDAO();

    /**
     * Self-service sign-up: always creates a {@link com.urbanissue.model.Citizen}.
     * Official and Admin accounts are created via database seed scripts or future admin tools.
     */
    public Optional<String> registerCitizen(String name, String email, String password, String phone) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            return Optional.of("Name, email and password are required.");
        }
        try {
            if (userDAO.findByEmail(email).isPresent()) {
                return Optional.of("This email is already registered. Sign in or use another email.");
            }
            User user = new com.urbanissue.model.Citizen(0, name, email, password, phone != null ? phone : "");
            userDAO.create(user);
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.of("Registration failed: " + friendlySqlMessage(e.getMessage()));
        }
    }

    /** @deprecated Use {@link #registerCitizen}; self-registration is Citizen-only. */
    @Deprecated
    public Optional<String> register(String name, String email, String password, String phone, String role) {
        return registerCitizen(name, email, password, phone);
    }

    public Optional<String> login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Optional.of("Email and password are required.");
        }

        if (SUPER_ADMIN_EMAIL.equalsIgnoreCase(email.trim()) && SUPER_ADMIN_PASSWORD.equals(password)) {
            User superAdmin = new com.urbanissue.model.Admin(-1, "Super Admin", SUPER_ADMIN_EMAIL, SUPER_ADMIN_PASSWORD, "");
            SessionManager.getInstance().setCurrentUser(superAdmin);
            return Optional.empty();
        }

        try {
            Optional<User> opt = userDAO.findByEmail(email);
            if (opt.isEmpty()) {
                return Optional.of("Invalid email or password.");
            }
            User user = opt.get();
            if (!user.getPassword().equals(password)) {
                return Optional.of("Invalid email or password.");
            }
            SessionManager.getInstance().setCurrentUser(user);
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.of("Login failed: " + friendlySqlMessage(e.getMessage()));
        }
    }

    private static String friendlySqlMessage(String raw) {
        if (raw == null) {
            return "Database error.";
        }
        if (raw.contains("Access denied for user")) {
            return "MySQL rejected the username/password. Update DBConfig.java to match your MySQL credentials.";
        }
        if (raw.contains("Unknown database")) {
            return "Database 'civictrack' does not exist. Run schema.sql first.";
        }
        if (isUnreachableMySql(raw)) {
            return "Cannot reach MySQL on the URL in use. "
                    + "Start the MySQL server (or MariaDB), confirm port 3306 is listening, "
                    + "and set CIVICTRACK_DB_URL / CIVICTRACK_DB_USERNAME / CIVICTRACK_DB_PASSWORD "
                    + "if your database is not on localhost. "
                    + "Create schema with schema.sql if you have not already.";
        }
        return raw;
    }

    private static boolean isUnreachableMySql(String raw) {
        String r = raw.toLowerCase();
        return raw.contains("Communications link failure")
                || raw.contains("Connection refused")
                || r.contains("connect timed out")
                || r.contains("connection timed out")
                || r.contains("no route to host")
                || r.contains("failed to connect");
    }

    public void logout() {
        SessionManager.getInstance().clear();
    }

}
