package com.urbanissue.controller;

import com.urbanissue.Main;
import com.urbanissue.model.Issue;
import com.urbanissue.service.AuthenticationService;
import com.urbanissue.service.IssueService;
import com.urbanissue.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CitizenDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Issue> issuesTable;
    @FXML private TableColumn<Issue, Number> idCol;
    @FXML private TableColumn<Issue, String> titleCol;
    @FXML private TableColumn<Issue, String> categoryCol;
    @FXML private TableColumn<Issue, String> statusCol;
    @FXML private TableColumn<Issue, String> priorityCol;
    @FXML private TableColumn<Issue, String> dateCol;
    @FXML private Button viewDetailButton;

    private final IssueService issueService = new IssueService();
    private final AuthenticationService authService = new AuthenticationService();
    private final java.util.Map<Integer, String> categoryNameCache = new java.util.HashMap<>();

    @FXML
    public void initialize() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) welcomeLabel.setText("Welcome, " + user.getName());
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIssueId()));
        titleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        categoryCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getCategoryName(c.getValue().getCategoryId())));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        priorityCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPriority()));
        dateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDateReported() != null ? c.getValue().getDateReported().format(DateTimeFormatter.ISO_LOCAL_DATE) : ""));
        issuesTable.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> viewDetailButton.setDisable(sel == null));
        refreshMyIssues();
    }

    private void refreshMyIssues() {
        categoryNameCache.clear(); // Clear cache to get fresh category names
        List<Issue> list = issueService.getMyReportedIssues();
        issuesTable.getItems().setAll(list);
    }

    private String getCategoryName(Integer categoryId) {
        if (categoryId == null) return "";

        // Check cache first
        if (categoryNameCache.containsKey(categoryId)) {
            return categoryNameCache.get(categoryId);
        }

        // Load from database via IssueService
        try {
            java.util.List<com.urbanissue.model.IssueCategory> categories = issueService.getAllCategories();
            for (com.urbanissue.model.IssueCategory category : categories) {
                categoryNameCache.put(category.getCategoryId(), category.getCategoryName());
                if (category.getCategoryId() == categoryId) {
                    return category.getCategoryName();
                }
            }
        } catch (Exception e) {
            // Log error but don't show to user for performance
        }

        categoryNameCache.put(categoryId, "Unknown Category");
        return "Unknown Category";
    }

    @FXML
    private void handleReportIssue() throws IOException {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/IssueForm.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack - Report Issue");
    }

    @FXML
    private void handleMyIssues() {
        refreshMyIssues();
    }

    @FXML
    private void handleViewDetail() throws IOException {
        Issue sel = issuesTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/urbanissue/fxml/IssueDetail.fxml"));
        Parent root = loader.load();
        IssueDetailController ctrl = loader.getController();
        ctrl.setIssueId(sel.getIssueId());
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack - Issue #" + sel.getIssueId());
    }

    @FXML
    private void handleLogout() throws IOException {
        authService.logout();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/Login.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack - Login");
    }
}
