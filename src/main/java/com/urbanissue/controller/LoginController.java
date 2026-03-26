package com.urbanissue.controller;

import com.urbanissue.Main;
import com.urbanissue.model.User;
import com.urbanissue.service.AuthenticationService;
import com.urbanissue.util.AlertHelper;
import com.urbanissue.util.ValidationHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        if (!ValidationHelper.isValidEmail(email)) {
            showMessage("Please enter a valid email.");
            return;
        }
        if (ValidationHelper.isBlank(password)) {
            showMessage("Please enter your password.");
            return;
        }
        Optional<String> error = authService.login(email.trim(), password);
        if (error.isPresent()) {
            showMessage(error.get());
            return;
        }
        navigateToRoleDashboard();
    }

    private User getCurrentUser() {
        return com.urbanissue.util.SessionManager.getInstance().getCurrentUser();
    }

    @FXML
    private void handleRegister() throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();

        // Preserve current window state
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        double currentX = stage.getX();
        double currentY = stage.getY();
        boolean isMaximized = stage.isMaximized();

        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/Register.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/com/urbanissue/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("CivicTrack - Register");

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

    private void showMessage(String msg) {
        messageLabel.setText(msg);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    private void navigateToRoleDashboard() {
        User user = getCurrentUser();
        if (user == null) return;
        String resource = switch (user.getRole().toUpperCase()) {
            case "ADMIN" -> "/com/urbanissue/fxml/AdminDashboard.fxml";
            case "OFFICIAL" -> "/com/urbanissue/fxml/OfficialDashboard.fxml";
            default -> "/com/urbanissue/fxml/CitizenDashboard.fxml";
        };
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();

            // Preserve current window state
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            double currentX = stage.getX();
            double currentY = stage.getY();
            boolean isMaximized = stage.isMaximized();

            Parent root = FXMLLoader.load(Main.class.getResource(resource));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/com/urbanissue/css/app.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("CivicTrack - " + user.getDisplayRole());

            // Restore window state
            if (isMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setWidth(currentWidth);
                stage.setHeight(currentHeight);
                stage.setX(currentX);
                stage.setY(currentY);
            }
            stage.setResizable(true);
        } catch (IOException e) {
            AlertHelper.showError("Navigation error", e.getMessage());
        }
    }
}
