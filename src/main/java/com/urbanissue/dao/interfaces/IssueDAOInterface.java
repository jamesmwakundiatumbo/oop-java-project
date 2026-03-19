package com.urbanissue.dao.interfaces;

import com.urbanissue.model.Issue;
import java.util.List;

/**
 * Contract for issue data access. Demonstrates: Abstraction (interface).
 */
public interface IssueDAOInterface {
    int create(Issue issue);
    Issue findById(int issueId);
    List<Issue> findAll();
    List<Issue> findByReportedBy(int userId);
    List<Issue> findByAssignedOfficial(int officialId);
    boolean update(Issue issue);
    boolean delete(int issueId);
}
