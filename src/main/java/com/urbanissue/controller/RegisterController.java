package com.urbanissue.controller;

import com.urbanissue.Main;
import com.urbanissue.service.AuthenticationService;
import com.urbanissue.util.ValidationHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
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
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label messageLabel;

    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("Citizen", "Official", "Admin");
        roleCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText() != null ? phoneField.getText().trim() : "";
        String role = "CITIZEN";
        if (roleCombo.getSelectionModel().getSelectedItem() != null) {
            role = switch (roleCombo.getSelectionModel().getSelectedItem().toLowerCase()) {
                case "official" -> "OFFICIAL";
                case "admin" -> "ADMIN";
                default -> "CITIZEN";
            };
        }
        if (ValidationHelper.isBlank(name)) { showMessage("Name is required."); return; }
        if (!ValidationHelper.isValidEmail(email)) { showMessage("Valid email is required."); return; }
        if (!ValidationHelper.isValidPasswordLength(password, 4)) { showMessage("Password must be at least 4 characters."); return; }
        Optional<String> error = authService.register(name.trim(), email.trim(), password, phone, role);
        if (error.isPresent()) {
            showMessage(error.get());
            return;
        }
        com.urbanissue.util.AlertHelper.showInfo("Success", "Registration successful. Please log in.");
        handleBack();
    }

    @FXML
    private void handleBack() throws IOException {
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/Login.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("CivicTrack - Login");
    }

    private void showMessage(String msg) {
        messageLabel.setText(msg);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }
}
