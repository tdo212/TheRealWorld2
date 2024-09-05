package com.therealworld.fitschedule.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterController {
    @FXML
    private VBox registerContainer;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private Button registerButton;
    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() {
        // Set focus on the VBox or any other element that doesn't accept user input
        Platform.runLater(() -> registerContainer.requestFocus());
    }
    @FXML
    public void clearFocus() {
        // Clear focus by requesting focus on an empty element like the root container
        registerContainer.requestFocus();
    }
    @FXML
    protected void onRegisterButtonClick() {
        // Logic to register the user
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();

        if (validateRegistration(username, password, confirmPassword, email, phoneNumber)) {
            // Register user in the system
            System.out.println("User registered successfully.");
            // After registration, redirect to login page or dashboard
        } else {
            // Handle validation failure
            System.out.println("Registration failed.");
        }
    }

    @FXML
    protected void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean validateRegistration(String username, String password, String confirmPassword, String email, String phoneNumber) {
        // Perform validation checks (e.g., matching passwords, non-empty fields, valid email format, etc.)
        return !username.isEmpty() && !password.isEmpty() && password.equals(confirmPassword) && !email.isEmpty() && !phoneNumber.isEmpty();
    }

    public void onBackToLoginClick(ActionEvent actionEvent) {
    }
}
