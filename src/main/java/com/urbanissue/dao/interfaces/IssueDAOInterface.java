package com.urbanissue.dao.interfaces;

import com.urbanissue.model.Issue;
import java.sql.SQLException;
import java.util.List;

/**
 * Contract for issue data access. Demonstrates: Abstraction (interface).
 */
public interface IssueDAOInterface {
    int create(Issue issue) throws SQLException;
    Issue findById(int issueId) throws SQLException;
    List<Issue> findAll() throws SQLException;
    List<Issue> findByReportedBy(int userId) throws SQLException;
    List<Issue> findByAssignedOfficial(int officialId) throws SQLException;
    boolean update(Issue issue) throws SQLException;
    boolean delete(int issueId) throws SQLException;
}
