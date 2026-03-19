package com.urbanissue.controller;

import com.urbanissue.Main;
import com.urbanissue.model.Comment;
import com.urbanissue.model.Issue;
import com.urbanissue.service.IssueService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class IssueDetailController {

    @FXML private Label titleLabel;
    @FXML private Label statusLabel;
    @FXML private Label priorityLabel;
    @FXML private Label locationLabel;
    @FXML private Label descriptionLabel;
    @FXML private ListView<String> commentsList;
    @FXML private TextField commentField;
    @FXML private Button addCommentButton;
    @FXML private Button updateStatusButton;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button backButton;

    private final IssueService issueService = new IssueService();
    private int issueId;
    private boolean showOfficialActions;
    private String backFxml = "/com/urbanissue/fxml/CitizenDashboard.fxml";

    public void setIssueId(int issueId) {
        this.issueId = issueId;
        loadIssue();
    }

    public void setShowOfficialActions(boolean show) {
        this.showOfficialActions = show;
        if (show) backFxml = "/com/urbanissue/fxml/OfficialDashboard.fxml";
    }

    @FXML
    public void initialize() {
        if (issueId > 0) loadIssue();
    }

    private void loadIssue() {
        if (issueId <= 0) return;
        Issue issue = issueService.getIssueWithDetails(issueId);
        if (issue == null) return;
        titleLabel.setText(issue.getTitle());
        statusLabel.setText(issue.getStatus());
        priorityLabel.setText(issue.getPriority());
        locationLabel.setText(issue.getLocation() != null ? issue.getLocation() : "");
        descriptionLabel.setText(issue.getDescription() != null ? issue.getDescription() : "");
        for (Comment c : issue.getComments()) {
            String line = c.getDateCreated() != null ? c.getDateCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " : "";
            line += c.getCommentText();
            commentsList.getItems().add(line);
        }
        if (showOfficialActions && updateStatusButton != null && statusCombo != null) {
            updateStatusButton.setVisible(true);
            statusCombo.setVisible(true);
            statusCombo.getItems().addAll("PENDING", "IN_PROGRESS", "RESOLVED");
            statusCombo.getSelectionModel().select(issue.getStatus());
        }
    }

    @FXML
    private void handleAddComment() {
        String text = commentField.getText();
        Optional<String> err = issueService.addComment(issueId, text);
        if (err.isPresent()) {
            com.urbanissue.util.AlertHelper.showError("Error", err.get());
            return;
        }
        commentField.clear();
        Issue issue = issueService.getIssueWithDetails(issueId);
        commentsList.getItems().clear();
        if (issue != null) {
            for (Comment c : issue.getComments()) {
                String line = (c.getDateCreated() != null ? c.getDateCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " : "") + c.getCommentText();
                commentsList.getItems().add(line);
            }
        }
    }

    @FXML
    private void handleUpdateStatus() {
        String status = statusCombo.getSelectionModel().getSelectedItem();
        if (status == null) return;
        issueService.updateStatus(issueId, status).ifPresentOrElse(
                err -> com.urbanissue.util.AlertHelper.showError("Error", err),
                () -> { statusLabel.setText(status); com.urbanissue.util.AlertHelper.showInfo("Done", "Status updated."); }
        );
    }

    @FXML
    private void handleBack() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        Parent root = FXMLLoader.load(Main.class.getResource(backFxml));
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack");
    }
}
