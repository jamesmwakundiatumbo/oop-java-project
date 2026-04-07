package com.urbanissue.service;

import com.urbanissue.dao.IssueDAO;
import com.urbanissue.dao.NotificationDAO;
import com.urbanissue.dao.UserDAO;
import com.urbanissue.model.Issue;
import com.urbanissue.model.Notification;
import com.urbanissue.model.User;

import java.sql.SQLException;
import java.util.List;

public class NotificationService {
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final IssueDAO issueDAO = new IssueDAO();
    private final UserDAO userDAO = new UserDAO();

    public void notifyNewIssue(Issue issue) {
        // Admin could be notified; for demo we just store for reporter if needed
    }

    public void notifyStatusChange(Issue issue) {
        if (issue.getReportedBy() == null) return;
        Notification n = new Notification();
        n.setUserId(issue.getReportedBy());
        n.setMessage("Issue #" + issue.getIssueId() + " status updated to: " + issue.getStatus());
        n.setRead(false);
        try {
            notificationDAO.create(n);
        } catch (SQLException ignored) {}
    }

    public void notifyAssignment(Issue issue, int officialId) {
        if (issue.getReportedBy() == null) return;
        Notification n = new Notification();
        n.setUserId(issue.getReportedBy());
        n.setMessage("Issue #" + issue.getIssueId() + " has been assigned to an official.");
        n.setRead(false);
        try {
            notificationDAO.create(n);
        } catch (SQLException ignored) {}
    }

    public void notifyNewComment(int issueId, int commenterId) {
        try {
            Issue issue = issueDAO.findById(issueId);
            if (issue == null || issue.getReportedBy() == null) return;
            if (issue.getReportedBy() == commenterId) return;
            Notification n = new Notification();
            n.setUserId(issue.getReportedBy());
            n.setMessage("New comment on Issue #" + issueId);
            n.setRead(false);
            notificationDAO.create(n);
        } catch (SQLException ignored) {}
    }

    public List<Notification> getNotificationsForCurrentUser(int userId) {
        try {
            return notificationDAO.findByUserId(userId);
        } catch (SQLException e) {
            return List.of();
        }
    }

    public void markAsRead(int notificationId) {
        try {
            notificationDAO.markAsRead(notificationId);
        } catch (SQLException ignored) {}
    }
}
