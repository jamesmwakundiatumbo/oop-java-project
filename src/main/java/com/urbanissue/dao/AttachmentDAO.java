package com.urbanissue.dao;

import com.urbanissue.db.DatabaseManager;
import com.urbanissue.model.Attachment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDAO {

    public int create(Attachment attachment) throws SQLException {
        String sql = "INSERT INTO attachments (issue_id, file_path) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, attachment.getIssueId());
            ps.setString(2, attachment.getFilePath());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    attachment.setAttachmentId(rs.getInt(1));
                    return attachment.getAttachmentId();
                }
            }
        }
        return -1;
    }

    public List<Attachment> findByIssueId(int issueId) throws SQLException {
        String sql = "SELECT attachment_id, issue_id, file_path FROM attachments WHERE issue_id = ?";
        List<Attachment> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Attachment(rs.getInt("attachment_id"), rs.getInt("issue_id"), rs.getString("file_path")));
                }
            }
        }
        return list;
    }
}
