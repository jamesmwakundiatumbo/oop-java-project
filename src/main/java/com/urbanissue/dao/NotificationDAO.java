package com.urbanissue.dao;

import com.urbanissue.db.DatabaseManager;
import com.urbanissue.model.Notification;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public int create(Notification n) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, message, is_read, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, n.getUserId());
            ps.setString(2, n.getMessage());
            ps.setBoolean(3, n.isRead());
            ps.setObject(4, n.getCreatedAt() != null ? n.getCreatedAt() : LocalDateTime.now());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    n.setNotificationId(rs.getInt(1));
                    return n.getNotificationId();
                }
            }
        }
        return -1;
    }

    public List<Notification> findByUserId(int userId) throws SQLException {
        String sql = "SELECT notification_id, user_id, message, is_read, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        List<Notification> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setNotificationId(rs.getInt("notification_id"));
                    n.setUserId(rs.getInt("user_id"));
                    n.setMessage(rs.getString("message"));
                    n.setRead(rs.getBoolean("is_read"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    n.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                    list.add(n);
                }
            }
        }
        return list;
    }

    public boolean markAsRead(int notificationId) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE notifications SET is_read = true WHERE notification_id = ?")) {
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        }
    }
}
