package com.urbanissue.model;

import java.time.LocalDateTime;

/**
 * Comment on an issue (Issue *-- Comment).
 */
public class Comment {
    private int commentId;
    private int issueId;
    private int userId;
    private String commentText;
    private LocalDateTime dateCreated;

    public Comment() {}

    public Comment(int commentId, int issueId, int userId, String commentText, LocalDateTime dateCreated) {
        this.commentId = commentId;
        this.issueId = issueId;
        this.userId = userId;
        this.commentText = commentText;
        this.dateCreated = dateCreated;
    }

    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }
    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
}
