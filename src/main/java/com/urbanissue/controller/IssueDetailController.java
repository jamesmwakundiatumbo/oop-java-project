package com.urbanissue.controller;

import com.urbanissue.Main;
import com.urbanissue.model.Attachment;
import com.urbanissue.model.Comment;
import com.urbanissue.model.Issue;
import com.urbanissue.service.IssueService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class IssueDetailController {

    @FXML private Label titleLabel;
    @FXML private Label statusLabel;
    @FXML private Label priorityLabel;
    @FXML private Label locationLabel;
    @FXML private Label descriptionLabel;
    @FXML private ScrollPane attachmentsPane;
    @FXML private VBox attachmentsContainer;
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

    public void setBackFxml(String backFxml) {
        this.backFxml = backFxml;
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

        // Load attachments
        loadAttachments(issue);

        // Load comments
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

    private void loadAttachments(Issue issue) {
        if (attachmentsContainer == null) return;

        // Clear existing attachments
        attachmentsContainer.getChildren().clear();

        // If no attachments, show a message
        if (issue.getAttachments() == null || issue.getAttachments().isEmpty()) {
            Label noAttachmentsLabel = new Label("No attachments");
            noAttachmentsLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            attachmentsContainer.getChildren().add(noAttachmentsLabel);
            return;
        }

        // Display each attachment
        for (Attachment attachment : issue.getAttachments()) {
            try {
                File file = new File(attachment.getFilePath());
                String fileName = file.getName();
                String extension = fileName.toLowerCase();

                HBox attachmentBox = new HBox(8);
                attachmentBox.setStyle("-fx-padding: 4; -fx-border-color: lightgray; -fx-border-radius: 4;");

                // Check if it's an image file
                if (extension.endsWith(".png") || extension.endsWith(".jpg") ||
                    extension.endsWith(".jpeg") || extension.endsWith(".gif") ||
                    extension.endsWith(".bmp")) {

                    if (file.exists()) {
                        try {
                            // Create thumbnail image
                            Image image = new Image(file.toURI().toString(), 100, 100, true, true);
                            ImageView imageView = new ImageView(image);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                            imageView.setCache(true);

                            // Add click handler to view full size
                            imageView.setOnMouseClicked(e -> showFullSizeImage(file));
                            imageView.setStyle("-fx-cursor: hand;");

                            Label fileLabel = new Label(fileName);
                            fileLabel.setStyle("-fx-font-size: 12px;");

                            VBox imageContainer = new VBox(4);
                            imageContainer.getChildren().addAll(imageView, fileLabel);
                            attachmentBox.getChildren().add(imageContainer);
                        } catch (Exception e) {
                            // If image loading fails, show as file
                            Label fileLabel = new Label("📷 " + fileName + " (Image file)");
                            attachmentBox.getChildren().add(fileLabel);
                        }
                    } else {
                        Label missingLabel = new Label("📷 " + fileName + " (File not found)");
                        missingLabel.setStyle("-fx-text-fill: red;");
                        attachmentBox.getChildren().add(missingLabel);
                    }
                } else {
                    // Non-image file
                    Label fileLabel = new Label("📎 " + fileName);
                    attachmentBox.getChildren().add(fileLabel);
                }

                attachmentsContainer.getChildren().add(attachmentBox);
            } catch (Exception e) {
                Label errorLabel = new Label("❌ Error loading attachment: " + attachment.getFilePath());
                errorLabel.setStyle("-fx-text-fill: red;");
                attachmentsContainer.getChildren().add(errorLabel);
            }
        }
    }

    private void showFullSizeImage(File imageFile) {
        try {
            Stage imageStage = new Stage();
            imageStage.setTitle("Image Viewer - " + imageFile.getName());

            Image fullImage = new Image(imageFile.toURI().toString());
            ImageView fullImageView = new ImageView(fullImage);
            fullImageView.setPreserveRatio(true);
            fullImageView.setSmooth(true);

            // Set reasonable max size but allow scaling
            double maxWidth = 800;
            double maxHeight = 600;
            if (fullImage.getWidth() > maxWidth || fullImage.getHeight() > maxHeight) {
                fullImageView.setFitWidth(maxWidth);
                fullImageView.setFitHeight(maxHeight);
            }

            ScrollPane scrollPane = new ScrollPane(fullImageView);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            Scene imageScene = new Scene(scrollPane);
            imageStage.setScene(imageScene);
            imageStage.show();
        } catch (Exception e) {
            com.urbanissue.util.AlertHelper.showError("Error", "Failed to open image: " + e.getMessage());
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
