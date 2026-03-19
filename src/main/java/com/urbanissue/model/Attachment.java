package com.urbanissue.model;

/**
 * File attachment for an issue (Issue *-- Attachment).
 */
public class Attachment {
    private int attachmentId;
    private int issueId;
    private String filePath;

    public Attachment() {}

    public Attachment(int attachmentId, int issueId, String filePath) {
        this.attachmentId = attachmentId;
        this.issueId = issueId;
        this.filePath = filePath;
    }

    public int getAttachmentId() { return attachmentId; }
    public void setAttachmentId(int attachmentId) { this.attachmentId = attachmentId; }
    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
