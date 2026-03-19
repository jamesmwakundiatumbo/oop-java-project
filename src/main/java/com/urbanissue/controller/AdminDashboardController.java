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

public class AdminDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Issue> issuesTable;
    @FXML private TableColumn<Issue, Number> idCol;
    @FXML private TableColumn<Issue, String> titleCol;
    @FXML private TableColumn<Issue, String> statusCol;
    @FXML private TableColumn<Issue, String> priorityCol;
    @FXML private TableColumn<Issue, Number> reportedByCol;
    @FXML private TableColumn<Issue, Number> assignedCol;
    @FXML private Button assignButton;
    @FXML private ComboBox<Official> officialCombo;

    private final IssueService issueService = new IssueService();
    private final AuthenticationService authService = new AuthenticationService();
    private final com.urbanissue.dao.UserDAO userDAO = new com.urbanissue.dao.UserDAO();

    @FXML
    public void initialize() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) welcomeLabel.setText("Admin: " + user.getName());
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIssueId()));
        titleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        priorityCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPriority()));
        reportedByCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getReportedBy() != null ? c.getValue().getReportedBy() : 0));
        assignedCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAssignedOfficial() != null ? c.getValue().getAssignedOfficial() : 0));
        officialCombo.setConverter(new javafx.util.StringConverter<Official>() {
            @Override
            public String toString(Official o) { return o != null ? o.getName() + " (ID " + o.getUserId() + ")" : ""; }
            @Override
            public Official fromString(String s) { return null; }
        });
        try {
            officialCombo.getItems().setAll(userDAO.findAllOfficials());
        } catch (Exception ignored) {}
        issuesTable.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> assignButton.setDisable(sel == null));
        refreshAllIssues();
    }

    private void refreshAllIssues() {
        List<Issue> list = issueService.getAllIssues();
        issuesTable.getItems().setAll(list);
    }

    @FXML
    private void handleAllIssues() {
        refreshAllIssues();
    }

    @FXML
    private void handleReports() throws IOException {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/Report.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack - Reports");
    }

    @FXML
    private void handleManageUsers() {
        com.urbanissue.util.AlertHelper.showInfo("Manage users", "User management screen can be added here (list users, edit roles).");
    }

    @FXML
    private void handleAssign() {
        Issue sel = issuesTable.getSelectionModel().getSelectedItem();
        Official off = officialCombo.getSelectionModel().getSelectedItem();
        if (sel == null || off == null) {
            com.urbanissue.util.AlertHelper.showError("Error", "Select an issue and an official.");
            return;
        }
        issueService.assignOfficial(sel.getIssueId(), off.getUserId()).ifPresentOrElse(
                err -> com.urbanissue.util.AlertHelper.showError("Error", err),
                () -> { com.urbanissue.util.AlertHelper.showInfo("Done", "Issue assigned."); refreshAllIssues(); }
        );
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
