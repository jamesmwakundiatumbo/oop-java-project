package com.urbanissue.dao;

import com.urbanissue.dao.interfaces.IssueDAOInterface;
import com.urbanissue.db.DatabaseManager;
import com.urbanissue.model.Issue;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IssueDAO implements IssueDAOInterface {

    @Override
    public int create(Issue issue) throws SQLException {
        String sql = "INSERT INTO issues (title, description, location, status, priority, date_reported, reported_by, assigned_official, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, issue.getTitle());
            ps.setString(2, issue.getDescription());
            ps.setString(3, issue.getLocation());
            ps.setString(4, issue.getStatus() != null ? issue.getStatus() : "PENDING");
            ps.setString(5, issue.getPriority() != null ? issue.getPriority() : "MEDIUM");
            ps.setObject(6, issue.getDateReported() != null ? issue.getDateReported() : LocalDateTime.now());
            ps.setObject(7, issue.getReportedBy());
            ps.setObject(8, issue.getAssignedOfficial());
            ps.setObject(9, issue.getCategoryId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    issue.setIssueId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public Issue findById(int issueId) throws SQLException {
        String sql = "SELECT issue_id, title, description, location, status, priority, date_reported, reported_by, assigned_official, category_id FROM issues WHERE issue_id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Issue> findAll() throws SQLException {
        String sql = "SELECT issue_id, title, description, location, status, priority, date_reported, reported_by, assigned_official, category_id FROM issues ORDER BY date_reported DESC";
        List<Issue> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Issue> findByReportedBy(int userId) throws SQLException {
        String sql = "SELECT issue_id, title, description, location, status, priority, date_reported, reported_by, assigned_official, category_id FROM issues WHERE reported_by = ? ORDER BY date_reported DESC";
        List<Issue> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Issue> findByAssignedOfficial(int officialId) throws SQLException {
        String sql = "SELECT issue_id, title, description, location, status, priority, date_reported, reported_by, assigned_official, category_id FROM issues WHERE assigned_official = ? ORDER BY date_reported DESC";
        List<Issue> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, officialId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public boolean update(Issue issue) throws SQLException {
        String sql = "UPDATE issues SET title=?, description=?, location=?, status=?, priority=?, reported_by=?, assigned_official=?, category_id=? WHERE issue_id=?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, issue.getTitle());
            ps.setString(2, issue.getDescription());
            ps.setString(3, issue.getLocation());
            ps.setString(4, issue.getStatus());
            ps.setString(5, issue.getPriority());
            ps.setObject(6, issue.getReportedBy());
            ps.setObject(7, issue.getAssignedOfficial());
            ps.setObject(8, issue.getCategoryId());
            ps.setInt(9, issue.getIssueId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int issueId) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM issues WHERE issue_id = ?")) {
            ps.setInt(1, issueId);
            return ps.executeUpdate() > 0;
        }
    }

    private Issue mapRow(ResultSet rs) throws SQLException {
        Issue i = new Issue();
        i.setIssueId(rs.getInt("issue_id"));
        i.setTitle(rs.getString("title"));
        i.setDescription(rs.getString("description"));
        i.setLocation(rs.getString("location"));
        i.setStatus(rs.getString("status"));
        i.setPriority(rs.getString("priority"));
        Timestamp ts = rs.getTimestamp("date_reported");
        i.setDateReported(ts != null ? ts.toLocalDateTime() : null);
        i.setReportedBy(rs.getObject("reported_by") != null ? rs.getInt("reported_by") : null);
        i.setAssignedOfficial(rs.getObject("assigned_official") != null ? rs.getInt("assigned_official") : null);
        i.setCategoryId(rs.getObject("category_id") != null ? rs.getInt("category_id") : null);
        return i;
    }
}
