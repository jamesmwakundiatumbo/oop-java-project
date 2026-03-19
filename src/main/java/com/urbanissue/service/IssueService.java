package com.urbanissue.service;

import com.urbanissue.dao.*;
import com.urbanissue.model.*;
import com.urbanissue.util.SessionManager;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for issues: create, update status, assign, load with comments/attachments.
 */
public class IssueService {
    private final IssueDAO issueDAO = new IssueDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final AttachmentDAO attachmentDAO = new AttachmentDAO();
    private final IssueCategoryDAO categoryDAO = new IssueCategoryDAO();
    private final NotificationService notificationService = new NotificationService();

    public List<IssueCategory> getAllCategories() {
        try {
            return categoryDAO.findAll();
        } catch (SQLException e) {
            return List.of();
        }
    }

    public Optional<String> reportIssue(String title, String description, String location, String priority, int categoryId, String filePath) {
        User current = SessionManager.getInstance().getCurrentUser();
        if (current == null) return Optional.of("You must be logged in as a citizen to report an issue.");
        int reportedBy = current.getUserId();
        if (title == null || title.isBlank()) return Optional.of("Title is required.");
        if (description == null || description.isBlank()) return Optional.of("Description is required.");

        Issue issue = new Issue();
        issue.setTitle(title.trim());
        issue.setDescription(description.trim());
        issue.setLocation(location != null ? location.trim() : "");
        issue.setPriority(priority != null ? priority : "MEDIUM");
        issue.setStatus("PENDING");
        issue.setDateReported(LocalDateTime.now());
        issue.setReportedBy(reportedBy);
        issue.setCategoryId(categoryId);

        try {
            issueDAO.create(issue);
            if (filePath != null && !filePath.isBlank()) {
                Attachment att = new Attachment(0, issue.getIssueId(), filePath);
                attachmentDAO.create(att);
            }
            notificationService.notifyNewIssue(issue);
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.of("Failed to report issue: " + e.getMessage());
        }
    }

    public List<Issue> getMyReportedIssues() {
        User u = SessionManager.getInstance().getCurrentUser();
        if (u == null) return List.of();
        try {
            return issueDAO.findByReportedBy(u.getUserId());
        } catch (SQLException e) {
            return List.of();
        }
    }

    public List<Issue> getAssignedIssues() {
        User u = SessionManager.getInstance().getCurrentUser();
        if (u == null) return List.of();
        try {
            return issueDAO.findByAssignedOfficial(u.getUserId());
        } catch (SQLException e) {
            return List.of();
        }
    }

    public List<Issue> getAllIssues() {
        try {
            return issueDAO.findAll();
        } catch (SQLException e) {
            return List.of();
        }
    }

    public Issue getIssueWithDetails(int issueId) {
        try {
            Issue issue = issueDAO.findById(issueId);
            if (issue != null) {
                issue.setComments(commentDAO.findByIssueId(issueId));
                issue.setAttachments(attachmentDAO.findByIssueId(issueId));
            }
            return issue;
        } catch (SQLException e) {
            return null;
        }
    }

    public Optional<String> updateStatus(int issueId, String newStatus) {
        try {
            Issue issue = issueDAO.findById(issueId);
            if (issue == null) return Optional.of("Issue not found.");
            issue.setStatus(newStatus);
            issueDAO.update(issue);
            notificationService.notifyStatusChange(issue);
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.of("Failed to update status: " + e.getMessage());
        }
    }

    public Optional<String> assignOfficial(int issueId, int officialId) {
        try {
            Issue issue = issueDAO.findById(issueId);
            if (issue == null) return Optional.of("Issue not found.");
            issue.setAssignedOfficial(officialId);
            issueDAO.update(issue);
            notificationService.notifyAssignment(issue, officialId);
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.of("Failed to assign: " + e.getMessage());
        }
    }

    public Optional<String> addComment(int issueId, String commentText) {
        User u = SessionManager.getInstance().getCurrentUser();
        if (u == null) return Optional.of("You must be logged in.");
        if (commentText == null || commentText.isBlank()) return Optional.of("Comment text is required.");

        Comment c = new Comment();
        c.setIssueId(issueId);
        c.setUserId(u.getUserId());
        c.setCommentText(commentText.trim());
        c.setDateCreated(LocalDateTime.now());

        try {
            commentDAO.create(c);
            notificationService.notifyNewComment(issueId, u.getUserId());
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.of("Failed to add comment: " + e.getMessage());
        }
    }
}
