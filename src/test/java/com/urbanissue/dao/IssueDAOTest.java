package com.urbanissue.dao;

import com.urbanissue.db.DatabaseManager;
import com.urbanissue.model.Issue;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration test: runs only when JDBC settings in {@link com.urbanissue.db.DBConfig} can connect.
 */
class IssueDAOTest {

    @Test
    void findAll_returnsList() throws Exception {
        assumeDbReachable();
        IssueDAO dao = new IssueDAO();
        List<Issue> list = dao.findAll();
        assertNotNull(list);
    }

    private static void assumeDbReachable() {
        try {
            DatabaseManager.getInstance().getConnection().close();
        } catch (SQLException e) {
            Assumptions.abort(
                    "Skipping DAO test (set CIVICTRACK_DB_URL / USERNAME / PASSWORD, run schema.sql): "
                            + e.getMessage());
        }
    }
}
