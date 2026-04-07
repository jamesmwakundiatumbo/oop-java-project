package com.urbanissue.service;

import com.urbanissue.dao.IssueDAO;
import com.urbanissue.model.Issue;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple reporting: aggregate issues by status, priority, etc. for Admin dashboard.
 */
public class ReportService {
    private final IssueDAO issueDAO = new IssueDAO();

    public List<Issue> getAllIssuesForReport() {
        try {
            return issueDAO.findAll();
        } catch (SQLException e) {
            return List.of();
        }
    }

    public Map<String, Long> getIssuesByStatus() {
        try {
            return issueDAO.findAll().stream()
                    .collect(Collectors.groupingBy(i -> i.getStatus() != null ? i.getStatus() : "UNKNOWN", Collectors.counting()));
        } catch (SQLException e) {
            return Map.of();
        }
    }

    public Map<String, Long> getIssuesByPriority() {
        try {
            return issueDAO.findAll().stream()
                    .collect(Collectors.groupingBy(i -> i.getPriority() != null ? i.getPriority() : "UNKNOWN", Collectors.counting()));
        } catch (SQLException e) {
            return Map.of();
        }
    }
}
