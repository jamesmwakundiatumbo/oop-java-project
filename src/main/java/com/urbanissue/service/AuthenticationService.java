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
    private final UserDAO userDAO = new UserDAO();

    public Optional<String> register(String name, String email, String password, String phone, String role) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            return Optional.of("Name, email and password are required.");
        }
        try {
            if (userDAO.findByEmail(email).isPresent()) {
                return Optional.of("Email already registered.");
            }
            User user = createUserByRole(role, name, email, password, phone);
            userDAO.create(user);
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.of("Registration failed: " + e.getMessage());
        }
    }

    public Optional<String> login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Optional.of("Email and password are required.");
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
            return Optional.of("Login failed: " + e.getMessage());
        }
    }

    public void logout() {
        SessionManager.getInstance().clear();
    }

    private User createUserByRole(String role, String name, String email, String password, String phone) {
        return switch (role != null ? role.toUpperCase() : "CITIZEN") {
            case "OFFICIAL" -> new com.urbanissue.model.Official(0, name, email, password, phone, null);
            case "ADMIN" -> new com.urbanissue.model.Admin(0, name, email, password, phone);
            default -> new com.urbanissue.model.Citizen(0, name, email, password, phone);
        };
    }
}
