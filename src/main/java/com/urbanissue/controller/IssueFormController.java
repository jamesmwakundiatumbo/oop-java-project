package com.urbanissue.controller;

import com.urbanissue.Main;
import com.urbanissue.model.IssueCategory;
import com.urbanissue.service.IssueService;
import com.urbanissue.util.FileUploadHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class IssueFormController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField locationField;
    @FXML private ComboBox<IssueCategory> categoryCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private Label fileLabel;
    @FXML private Label messageLabel;

    private final IssueService issueService = new IssueService();
    private String selectedFilePath;

    @FXML
    public void initialize() {
        List<IssueCategory> cats = issueService.getAllCategories();
        categoryCombo.getItems().setAll(cats);
        categoryCombo.setConverter(new javafx.util.StringConverter<IssueCategory>() {
            @Override
            public String toString(IssueCategory c) { return c != null ? c.getCategoryName() : ""; }
            @Override
            public IssueCategory fromString(String s) { return null; }
        });
        priorityCombo.getItems().addAll("LOW", "MEDIUM", "HIGH");
        priorityCombo.getSelectionModel().select("MEDIUM");
    }

    @FXML
    private void handleUpload() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        selectedFilePath = FileUploadHelper.chooseAndSaveFile(stage);
        fileLabel.setText(selectedFilePath != null ? "File selected" : "");
    }

    @FXML
    private void handleSubmit() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String location = locationField.getText();
        String priority = priorityCombo.getSelectionModel().getSelectedItem();
        IssueCategory cat = categoryCombo.getSelectionModel().getSelectedItem();
        int categoryId = cat != null ? cat.getCategoryId() : 0;
        Optional<String> err = issueService.reportIssue(title, description, location, priority, categoryId, selectedFilePath);
        if (err.isPresent()) {
            messageLabel.setText(err.get());
            messageLabel.setVisible(true);
            messageLabel.setManaged(true);
            return;
        }
        com.urbanissue.util.AlertHelper.showInfo("Success", "Issue reported successfully.");
        try {
            handleCancel();
        } catch (IOException e) {
            messageLabel.setText("Navigation error: " + e.getMessage());
            messageLabel.setVisible(true);
            messageLabel.setManaged(true);
        }
    }

    @FXML
    private void handleCancel() throws IOException {
        Stage stage = (Stage) titleField.getScene().getWindow();

        // Preserve current window state
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        double currentX = stage.getX();
        double currentY = stage.getY();
        boolean isMaximized = stage.isMaximized();

        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/CitizenDashboard.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/com/urbanissue/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("CivicTrack - Dashboard");

        // Restore window state
        if (isMaximized) {
            stage.setMaximized(true);
        } else {
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
            stage.setX(currentX);
            stage.setY(currentY);
        }
    }
}
