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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class OfficialDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Issue> issuesTable;
    @FXML private TableColumn<Issue, Number> idCol;
    @FXML private TableColumn<Issue, String> titleCol;
    @FXML private TableColumn<Issue, String> categoryCol;
    @FXML private TableColumn<Issue, String> statusCol;
    @FXML private TableColumn<Issue, String> priorityCol;
    @FXML private Button viewDetailButton;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button updateStatusButton;

    // Filter controls
    @FXML private ComboBox<String> categoryFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> priorityFilterCombo;
    @FXML private Button applyFiltersButton;
    @FXML private Button clearFiltersButton;

    private final IssueService issueService = new IssueService();
    private final AuthenticationService authService = new AuthenticationService();
    private final java.util.Map<Integer, String> categoryNameCache = new java.util.HashMap<>();

    // Store original unfiltered list
    private List<Issue> allIssuesList = new java.util.ArrayList<>();

    @FXML
    public void initialize() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) welcomeLabel.setText("Welcome, " + user.getName());
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIssueId()));
        titleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        categoryCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getCategoryName(c.getValue().getCategoryId())));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        priorityCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPriority()));

        // Setup custom cell renderers for badges
        statusCol.setCellFactory(column -> new javafx.scene.control.TableCell<Issue, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    javafx.scene.control.Label badge = new javafx.scene.control.Label(status);
                    badge.getStyleClass().addAll("status-badge", "status-" + status.toLowerCase().replace("_", "-"));
                    setGraphic(badge);
                }
            }
        });

        priorityCol.setCellFactory(column -> new javafx.scene.control.TableCell<Issue, String>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setGraphic(null);
                } else {
                    javafx.scene.control.Label badge = new javafx.scene.control.Label(priority);
                    badge.getStyleClass().addAll("priority-badge", "priority-" + priority.toLowerCase());
                    setGraphic(badge);
                }
            }
        });

        statusCombo.getItems().addAll("PENDING", "IN_PROGRESS", "RESOLVED");
        issuesTable.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            viewDetailButton.setDisable(sel == null);
            updateStatusButton.setDisable(sel == null);
            if (sel != null) statusCombo.getSelectionModel().select(sel.getStatus());
        });

        // Setup filter controls
        setupFilterControls();

        refreshAssigned();
    }

    private void setupFilterControls() {
        // Setup category filter
        if (categoryFilterCombo != null) {
            try {
                List<com.urbanissue.model.IssueCategory> categories = issueService.getAllCategories();
                categoryFilterCombo.getItems().add("All Categories");
                for (com.urbanissue.model.IssueCategory category : categories) {
                    categoryFilterCombo.getItems().add(category.getCategoryName());
                }
                categoryFilterCombo.getSelectionModel().selectFirst();
            } catch (Exception e) {
                System.err.println("Failed to load categories for filter: " + e.getMessage());
            }
        }

        // Setup status filter
        if (statusFilterCombo != null) {
            statusFilterCombo.getItems().addAll("All Statuses", "PENDING", "IN_PROGRESS", "RESOLVED", "REJECTED", "CLOSED");
            statusFilterCombo.getSelectionModel().selectFirst();
        }

        // Setup priority filter
        if (priorityFilterCombo != null) {
            priorityFilterCombo.getItems().addAll("All Priorities", "LOW", "MEDIUM", "HIGH");
            priorityFilterCombo.getSelectionModel().selectFirst();
        }
    }

    private void refreshAssigned() {
        categoryNameCache.clear(); // Clear cache to get fresh category names
        allIssuesList = issueService.getAssignedIssues();
        issuesTable.getItems().setAll(allIssuesList);
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
    private void handleViewDetail() throws IOException {
        Issue sel = issuesTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/urbanissue/fxml/IssueDetail.fxml"));
        Parent root = loader.load();
        IssueDetailController ctrl = loader.getController();
        ctrl.setIssueId(sel.getIssueId());
        ctrl.setShowOfficialActions(true);
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();

        // Preserve current window state
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        double currentX = stage.getX();
        double currentY = stage.getY();
        boolean isMaximized = stage.isMaximized();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/com/urbanissue/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("CivicTrack - Issue #" + sel.getIssueId());

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

    @FXML
    private void handleUpdateStatus() {
        Issue sel = issuesTable.getSelectionModel().getSelectedItem();
        String status = statusCombo.getSelectionModel().getSelectedItem();
        if (sel == null || status == null) return;
        issueService.updateStatus(sel.getIssueId(), status).ifPresentOrElse(
                err -> com.urbanissue.util.AlertHelper.showError("Error", err),
                this::refreshAssigned
        );
    }

    @FXML
    private void handleLogout() throws IOException {
        authService.logout();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();

        // Preserve current window state
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        double currentX = stage.getX();
        double currentY = stage.getY();
        boolean isMaximized = stage.isMaximized();

        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/com/urbanissue/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("CivicTrack - Login");

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

    @FXML
    private void handleApplyFilters() {
        List<Issue> filteredList = new java.util.ArrayList<>(allIssuesList);

        // Filter by category
        String selectedCategory = categoryFilterCombo != null ? categoryFilterCombo.getSelectionModel().getSelectedItem() : null;
        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            filteredList = filteredList.stream()
                .filter(issue -> {
                    String categoryName = getCategoryName(issue.getCategoryId());
                    return selectedCategory.equals(categoryName);
                })
                .collect(java.util.stream.Collectors.toList());
        }

        // Filter by status
        String selectedStatus = statusFilterCombo != null ? statusFilterCombo.getSelectionModel().getSelectedItem() : null;
        if (selectedStatus != null && !selectedStatus.equals("All Statuses")) {
            filteredList = filteredList.stream()
                .filter(issue -> selectedStatus.equals(issue.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        }

        // Filter by priority
        String selectedPriority = priorityFilterCombo != null ? priorityFilterCombo.getSelectionModel().getSelectedItem() : null;
        if (selectedPriority != null && !selectedPriority.equals("All Priorities")) {
            filteredList = filteredList.stream()
                .filter(issue -> selectedPriority.equals(issue.getPriority()))
                .collect(java.util.stream.Collectors.toList());
        }

        issuesTable.getItems().setAll(filteredList);
    }

    @FXML
    private void handleClearFilters() {
        // Reset all filter selections
        if (categoryFilterCombo != null) {
            categoryFilterCombo.getSelectionModel().selectFirst(); // Select "All Categories"
        }
        if (statusFilterCombo != null) {
            statusFilterCombo.getSelectionModel().selectFirst(); // Select "All Statuses"
        }
        if (priorityFilterCombo != null) {
            priorityFilterCombo.getSelectionModel().selectFirst(); // Select "All Priorities"
        }

        // Show all issues
        issuesTable.getItems().setAll(allIssuesList);
    }
}
