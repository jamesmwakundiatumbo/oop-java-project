package com.urbanissue.dao;

import com.urbanissue.db.DatabaseManager;
import com.urbanissue.model.Comment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    public int create(Comment comment) throws SQLException {
        String sql = "INSERT INTO comments (issue_id, user_id, comment_text, date_created) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, comment.getIssueId());
            ps.setInt(2, comment.getUserId());
            ps.setString(3, comment.getCommentText());
            ps.setObject(4, comment.getDateCreated() != null ? comment.getDateCreated() : LocalDateTime.now());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    comment.setCommentId(rs.getInt(1));
                    return comment.getCommentId();
                }
            }
        }
        return -1;
    }

    public List<Comment> findByIssueId(int issueId) throws SQLException {
        String sql = "SELECT comment_id, issue_id, user_id, comment_text, date_created FROM comments WHERE issue_id = ? ORDER BY date_created";
        List<Comment> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment c = new Comment();
                    c.setCommentId(rs.getInt("comment_id"));
                    c.setIssueId(rs.getInt("issue_id"));
                    c.setUserId(rs.getInt("user_id"));
                    c.setCommentText(rs.getString("comment_text"));
                    Timestamp ts = rs.getTimestamp("date_created");
                    c.setDateCreated(ts != null ? ts.toLocalDateTime() : null);
                    list.add(c);
                }
            }
        }
        return list;
    }
}
