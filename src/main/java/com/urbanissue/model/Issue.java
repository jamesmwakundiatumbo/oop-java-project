package com.urbanissue.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Urban issue reported by a Citizen, can be assigned to an Official.
 * Issue *-- Comment, Issue *-- Attachment (composition).
 */
public class Issue {
    private int issueId;
    private String title;
    private String description;
    private String location;
    private String status;   // e.g. PENDING, IN_PROGRESS, RESOLVED
    private String priority; // e.g. LOW, MEDIUM, HIGH
    private LocalDateTime dateReported;
    private Integer reportedBy;      // FK to User (Citizen)
    private Integer assignedOfficial; // FK to User (Official)
    private Integer categoryId;      // FK to IssueCategory
    private List<Comment> comments = new ArrayList<>();
    private List<Attachment> attachments = new ArrayList<>();

    public Issue() {}

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public LocalDateTime getDateReported() { return dateReported; }
    public void setDateReported(LocalDateTime dateReported) { this.dateReported = dateReported; }
    public Integer getReportedBy() { return reportedBy; }
    public void setReportedBy(Integer reportedBy) { this.reportedBy = reportedBy; }
    public Integer getAssignedOfficial() { return assignedOfficial; }
    public void setAssignedOfficial(Integer assignedOfficial) { this.assignedOfficial = assignedOfficial; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }
}
