package com.urbanissue.controller;

import com.urbanissue.Main;
import com.urbanissue.service.ReportService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportController {

    @FXML private Label statusReportLabel;
    @FXML private Label priorityReportLabel;

    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        Map<String, Long> byStatus = reportService.getIssuesByStatus();
        Map<String, Long> byPriority = reportService.getIssuesByPriority();
        statusReportLabel.setText("By status: " + formatMap(byStatus));
        priorityReportLabel.setText("By priority: " + formatMap(byPriority));
    }

    private String formatMap(Map<String, Long> map) {
        if (map.isEmpty()) return "No data.";
        return map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(", "));
    }

    @FXML
    private void handleBack() throws IOException {
        Stage stage = (Stage) statusReportLabel.getScene().getWindow();
        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/AdminDashboard.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack - Admin");
    }
}
