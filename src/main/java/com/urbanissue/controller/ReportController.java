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

        // Preserve current window state
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        double currentX = stage.getX();
        double currentY = stage.getY();
        boolean isMaximized = stage.isMaximized();

        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/AdminDashboard.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/com/urbanissue/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("CivicTrack - Admin");

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
