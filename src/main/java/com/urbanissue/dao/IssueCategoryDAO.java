package com.urbanissue.dao;

import com.urbanissue.db.DatabaseManager;
import com.urbanissue.model.IssueCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueCategoryDAO {

    public List<IssueCategory> findAll() throws SQLException {
        String sql = "SELECT category_id, category_name FROM issue_categories ORDER BY category_name";
        List<IssueCategory> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new IssueCategory(rs.getInt("category_id"), rs.getString("category_name")));
            }
        }
        return list;
    }

    public IssueCategory findById(int id) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT category_id, category_name FROM issue_categories WHERE category_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new IssueCategory(rs.getInt("category_id"), rs.getString("category_name"));
            }
        }
        return null;
    }
}
