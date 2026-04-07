package com.urbanissue.controller;

import com.urbanissue.Main;
import com.urbanissue.service.AuthenticationService;
import com.urbanissue.util.UserGuideHelper;
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

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private Label messageLabel;

    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    private void handleUserGuide() {
        UserGuideHelper.show((Stage) nameField.getScene().getWindow());
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText() != null ? phoneField.getText().trim() : "";
        if (ValidationHelper.isBlank(name)) { showMessage("Name is required."); return; }
        if (!ValidationHelper.isValidEmail(email)) { showMessage("Valid email is required."); return; }
        if (!ValidationHelper.isValidPasswordLength(password, 4)) { showMessage("Password must be at least 4 characters."); return; }
        Optional<String> error = authService.registerCitizen(name.trim(), email.trim(), password, phone);
        if (error.isPresent()) {
            showMessage(error.get());
            return;
        }
        com.urbanissue.util.AlertHelper.showInfo("Success", "Registration successful. Please log in.");
        try {
            handleBack();
        } catch (IOException e) {
            showMessage("Navigation error: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() throws IOException {
        Stage stage = (Stage) nameField.getScene().getWindow();

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

    private void showMessage(String msg) {
        messageLabel.setText(msg);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }
}
