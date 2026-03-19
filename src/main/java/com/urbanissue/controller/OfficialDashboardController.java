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
    @FXML private TableColumn<Issue, String> statusCol;
    @FXML private TableColumn<Issue, String> priorityCol;
    @FXML private Button viewDetailButton;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button updateStatusButton;

    private final IssueService issueService = new IssueService();
    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    public void initialize() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) welcomeLabel.setText("Welcome, " + user.getName());
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIssueId()));
        titleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        priorityCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPriority()));
        statusCombo.getItems().addAll("PENDING", "IN_PROGRESS", "RESOLVED");
        issuesTable.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            viewDetailButton.setDisable(sel == null);
            updateStatusButton.setDisable(sel == null);
            if (sel != null) statusCombo.getSelectionModel().select(sel.getStatus());
        });
        refreshAssigned();
    }

    private void refreshAssigned() {
        List<Issue> list = issueService.getAssignedIssues();
        issuesTable.getItems().setAll(list);
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
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack - Issue #" + sel.getIssueId());
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
        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/Login.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack - Login");
    }
}
