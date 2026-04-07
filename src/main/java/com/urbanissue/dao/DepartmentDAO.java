package com.urbanissue.dao;

import com.urbanissue.db.DatabaseManager;
import com.urbanissue.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<Department> findAll() throws SQLException {
        String sql = "SELECT department_id, department_name FROM departments ORDER BY department_name";
        List<Department> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Department d = new Department(rs.getInt("department_id"), rs.getString("department_name"));
                list.add(d);
            }
        }
        return list;
    }

    public Department findById(int id) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT department_id, department_name FROM departments WHERE department_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Department(rs.getInt("department_id"), rs.getString("department_name"));
            }
        }
        return null;
    }
}
