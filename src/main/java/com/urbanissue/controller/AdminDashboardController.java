package com.urbanissue.controller;

import com.urbanissue.Main;
import com.urbanissue.model.Issue;
import com.urbanissue.model.Official;
import com.urbanissue.model.User;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;

    // Issues tab
    @FXML private TableView<Issue> issuesTable;
    @FXML private TableColumn<Issue, Number> idCol;
    @FXML private TableColumn<Issue, String> titleCol;
    @FXML private TableColumn<Issue, String> categoryCol;
    @FXML private TableColumn<Issue, String> statusCol;
    @FXML private TableColumn<Issue, String> priorityCol;
    @FXML private TableColumn<Issue, String> reportedByCol;
    @FXML private TableColumn<Issue, String> assignedCol;
    @FXML private Button viewDetailButton;
    @FXML private Button assignIssueButton;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button updateStatusButton;

    // Users tab
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Number> userIdCol;
    @FXML private TableColumn<User, String> userNameCol;
    @FXML private TableColumn<User, String> userEmailCol;
    @FXML private TableColumn<User, String> userRoleCol;
    @FXML private Button addUserButton;
    @FXML private Button editUserButton;
    @FXML private Button deleteUserButton;

    // Statistics tab
    @FXML private Label totalIssuesLabel;
    @FXML private Label pendingIssuesLabel;
    @FXML private Label resolvedIssuesLabel;
    @FXML private Label totalUsersLabel;
    @FXML private TableView<CategoryStatistic> categoryStatsTable;
    @FXML private TableColumn<CategoryStatistic, String> categoryNameCol;
    @FXML private TableColumn<CategoryStatistic, Number> categoryCountCol;
    @FXML private Button refreshStatsButton;

    private final IssueService issueService = new IssueService();
    private final AuthenticationService authService = new AuthenticationService();
    private final com.urbanissue.dao.UserDAO userDAO = new com.urbanissue.dao.UserDAO();
    private final java.util.Map<Integer, String> userNameCache = new java.util.HashMap<>();
    private final java.util.Map<Integer, String> categoryNameCache = new java.util.HashMap<>();

    @FXML
    public void initialize() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) welcomeLabel.setText("Admin Dashboard - " + user.getName());

        // Setup issues table
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIssueId()));
        titleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        categoryCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getCategoryName(c.getValue().getCategoryId())));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        priorityCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPriority()));
        reportedByCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getUserName(c.getValue().getReportedBy())));
        assignedCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getUserName(c.getValue().getAssignedOfficial())));

        // Setup users table
        if (usersTable != null) {
            userIdCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getUserId()));
            userNameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
            userEmailCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
            userRoleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRole()));
        }

        // Setup category statistics table
        if (categoryStatsTable != null) {
            categoryNameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCategoryName()));
            categoryCountCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCount()));
        }

        // Setup status combo
        if (statusCombo != null) {
            statusCombo.getItems().addAll("PENDING", "IN_PROGRESS", "RESOLVED", "CLOSED");
        }

        // Setup selection listeners
        issuesTable.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            boolean hasSelection = sel != null;
            if (viewDetailButton != null) viewDetailButton.setDisable(!hasSelection);
            if (assignIssueButton != null) assignIssueButton.setDisable(!hasSelection);
            if (updateStatusButton != null) updateStatusButton.setDisable(!hasSelection);
        });

        if (usersTable != null) {
            usersTable.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
                boolean hasSelection = sel != null;
                if (editUserButton != null) editUserButton.setDisable(!hasSelection);
                if (deleteUserButton != null) deleteUserButton.setDisable(!hasSelection);
            });
        }

        refreshAllIssues();
        refreshUsers();
        refreshStats();
    }

    private String getUserName(Integer userId) {
        if (userId == null) return "";

        // Check cache first
        if (userNameCache.containsKey(userId)) {
            return userNameCache.get(userId);
        }

        // Load from database
        try {
            java.util.Optional<User> user = userDAO.findById(userId);
            if (user.isPresent()) {
                String name = user.get().getName();
                userNameCache.put(userId, name);
                return name;
            }
        } catch (Exception e) {
            // Log error but don't show to user for performance
        }

        userNameCache.put(userId, "Unknown User");
        return "Unknown User";
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

    private void refreshAllIssues() {
        userNameCache.clear(); // Clear cache to get fresh user names
        categoryNameCache.clear(); // Clear cache to get fresh category names
        List<Issue> list = issueService.getAllIssues();
        issuesTable.getItems().setAll(list);
    }

    private void refreshUsers() {
        if (usersTable != null) {
            try {
                List<User> users = userDAO.findAll();
                usersTable.getItems().setAll(users);
            } catch (Exception e) {
                com.urbanissue.util.AlertHelper.showError("Error", "Failed to load users: " + e.getMessage());
            }
        }
    }

    private void refreshStats() {
        try {
            List<Issue> allIssues = issueService.getAllIssues();
            if (totalIssuesLabel != null) totalIssuesLabel.setText(String.valueOf(allIssues.size()));

            long pending = allIssues.stream().filter(i -> "PENDING".equals(i.getStatus())).count();
            if (pendingIssuesLabel != null) pendingIssuesLabel.setText(String.valueOf(pending));

            long resolved = allIssues.stream().filter(i -> "RESOLVED".equals(i.getStatus()) || "CLOSED".equals(i.getStatus())).count();
            if (resolvedIssuesLabel != null) resolvedIssuesLabel.setText(String.valueOf(resolved));

            List<User> allUsers = userDAO.findAll();
            if (totalUsersLabel != null) totalUsersLabel.setText(String.valueOf(allUsers.size()));

            // Category statistics
            refreshCategoryStats(allIssues);
        } catch (Exception e) {
            com.urbanissue.util.AlertHelper.showError("Error", "Failed to load statistics: " + e.getMessage());
        }
    }

    private void refreshCategoryStats(List<Issue> allIssues) {
        if (categoryStatsTable == null) return;

        try {
            // Get all categories
            List<com.urbanissue.model.IssueCategory> categories = issueService.getAllCategories();

            // Count issues by category
            Map<Integer, Long> issueCountByCategory = allIssues.stream()
                .filter(issue -> issue.getCategoryId() != null)
                .collect(Collectors.groupingBy(Issue::getCategoryId, Collectors.counting()));

            // Create CategoryStatistic objects
            List<CategoryStatistic> categoryStats = categories.stream()
                .map(category -> new CategoryStatistic(
                    category.getCategoryName(),
                    issueCountByCategory.getOrDefault(category.getCategoryId(), 0L).intValue()
                ))
                .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount())) // Sort by count descending
                .collect(Collectors.toList());

            // Add "Uncategorized" if there are issues without categories
            long uncategorizedCount = allIssues.stream()
                .filter(issue -> issue.getCategoryId() == null)
                .count();
            if (uncategorizedCount > 0) {
                categoryStats.add(new CategoryStatistic("Uncategorized", (int) uncategorizedCount));
            }

            categoryStatsTable.getItems().setAll(categoryStats);
        } catch (Exception e) {
            com.urbanissue.util.AlertHelper.showError("Error", "Failed to load category statistics: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewDetail() throws IOException {
        Issue selected = issuesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Navigate to IssueDetail screen like other dashboards
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/urbanissue/fxml/IssueDetail.fxml"));
        Parent root = loader.load();
        IssueDetailController ctrl = loader.getController();
        ctrl.setIssueId(selected.getIssueId());
        // Set back navigation to admin dashboard
        ctrl.setBackFxml("/com/urbanissue/fxml/AdminDashboard.fxml");
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack - Issue #" + selected.getIssueId());
    }

    @FXML
    private void handleAssignIssue() {
        Issue selected = issuesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            // Get all officials for assignment
            java.util.List<com.urbanissue.model.Official> officials = userDAO.findAllOfficials();
            if (officials.isEmpty()) {
                com.urbanissue.util.AlertHelper.showError("Error", "No officials available for assignment");
                return;
            }

            // Create choice dialog
            javafx.scene.control.ChoiceDialog<com.urbanissue.model.Official> dialog =
                new javafx.scene.control.ChoiceDialog<>(officials.get(0), officials);
            dialog.setTitle("Assign Issue");
            dialog.setHeaderText("Assign issue #" + selected.getIssueId() + " to an official");
            dialog.setContentText("Select official:");

            // The dialog will use the toString() method of Official model for display

            // Show dialog and handle result
            java.util.Optional<com.urbanissue.model.Official> result = dialog.showAndWait();
            result.ifPresent(official -> {
                issueService.assignOfficial(selected.getIssueId(), official.getUserId()).ifPresentOrElse(
                    err -> com.urbanissue.util.AlertHelper.showError("Error", err),
                    () -> {
                        com.urbanissue.util.AlertHelper.showInfo("Success",
                            "Issue #" + selected.getIssueId() + " assigned to " + official.getName());
                        refreshAllIssues();
                    }
                );
            });
        } catch (Exception e) {
            com.urbanissue.util.AlertHelper.showError("Error", "Failed to load officials: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateStatus() {
        Issue selected = issuesTable.getSelectionModel().getSelectedItem();
        String newStatus = statusCombo.getSelectionModel().getSelectedItem();
        if (selected == null || newStatus == null) {
            com.urbanissue.util.AlertHelper.showError("Error", "Please select an issue and a status");
            return;
        }

        issueService.updateStatus(selected.getIssueId(), newStatus).ifPresentOrElse(
            err -> com.urbanissue.util.AlertHelper.showError("Error", err),
            () -> {
                com.urbanissue.util.AlertHelper.showInfo("Success", "Status updated successfully");
                refreshAllIssues();
                refreshStats();
            }
        );
    }

    @FXML
    private void handleAddUser() {
        com.urbanissue.util.AlertHelper.showInfo("Add User", "Add user feature to be implemented");
    }

    @FXML
    private void handleEditUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        com.urbanissue.util.AlertHelper.showInfo("Edit User", "Edit user feature to be implemented for: " + selected.getName());
    }

    @FXML
    private void handleDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        com.urbanissue.util.AlertHelper.showInfo("Delete User", "Delete user feature to be implemented for: " + selected.getName());
    }

    @FXML
    private void handleRefreshStats() {
        refreshStats();
        com.urbanissue.util.AlertHelper.showInfo("Refreshed", "Statistics refreshed successfully");
    }

    @FXML
    private void handleLogout() throws IOException {
        authService.logout();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/Login.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack - Login");
    }

    // Inner class for category statistics data
    public static class CategoryStatistic {
        private final String categoryName;
        private final int count;

        public CategoryStatistic(String categoryName, int count) {
            this.categoryName = categoryName;
            this.count = count;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public int getCount() {
            return count;
        }
    }
}
