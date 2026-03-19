package com.urbanissue.dao;

import com.urbanissue.db.DatabaseManager;
import com.urbanissue.model.Admin;
import com.urbanissue.model.Citizen;
import com.urbanissue.model.Official;
import com.urbanissue.model.User;

import java.sql.*;
import java.util.Optional;

/**
 * DAO for User (Citizen, Official, Admin). Uses polymorphism when reading by role.
 */
public class UserDAO {

    public int create(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, phone, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    user.setUserId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, name, email, password, phone, role FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT user_id, name, email, password, phone, role FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    /** Polymorphism: build correct subclass from role. */
    private User mapToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        int userId = rs.getInt("user_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String phone = rs.getString("phone");

        return switch (role != null ? role.toUpperCase() : "") {
            case "CITIZEN" -> new Citizen(userId, name, email, password, phone);
            case "OFFICIAL" -> {
                Official o = new Official(userId, name, email, password, phone, null);
                // Department ID can be loaded separately if you add it to users table
                yield o;
            }
            case "ADMIN" -> new Admin(userId, name, email, password, phone);
            default -> new Citizen(userId, name, email, password, phone);
        };
    }

    public java.util.List<User> findAll() throws SQLException {
        String sql = "SELECT user_id, name, email, password, phone, role FROM users ORDER BY user_id";
        java.util.List<User> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapToUser(rs));
            }
        }
        return list;
    }

    public java.util.List<Official> findAllOfficials() throws SQLException {
        String sql = "SELECT user_id, name, email, password, phone, role FROM users WHERE role = 'OFFICIAL' ORDER BY name";
        java.util.List<Official> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User u = mapToUser(rs);
                if (u instanceof Official o) list.add(o);
            }
        }
        return list;
    }
}
