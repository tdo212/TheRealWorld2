package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.SqliteUserDAO;
import com.therealworld.fitschedule.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class RegisterController {

    private SqliteUserDAO userDAO;

    private static final String REGISTRATION_SUCCESS = "User registered successfully.";
    private static final String REGISTRATION_FAILED = "Registration failed.";

    public RegisterController() {
        this.userDAO = new SqliteUserDAO(); // Default behavior
    }

    // Setter for injecting a mock DAO in tests
    public void setUserDAO(SqliteUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @FXML
    protected TextField usernameField;
    @FXML
    protected PasswordField passwordField;
    @FXML
    protected PasswordField confirmPasswordField;
    @FXML
    protected TextField emailField;
    @FXML
    protected TextField phoneNumberField;
    @FXML
    protected Button registerButton;
    @FXML
    protected Button backToLoginButton;

    @FXML
    public void initialize() {
        Platform.runLater(() -> registerButton.getScene().getRoot().requestFocus());
    }

    @FXML
    protected void onRegisterButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();

        String errorMessage = getValidationErrorMessage(username, password, confirmPassword, email, phoneNumber);

        if (errorMessage == null) {
            userDAO.addUser(username, password, email, phoneNumber);
            showAlert(REGISTRATION_SUCCESS, Alert.AlertType.INFORMATION);
        } else {
            showAlert("Registration failed: " + errorMessage, Alert.AlertType.ERROR);
        }
    }

    private String getValidationErrorMessage(String username, String password, String confirmPassword, String email, String phoneNumber) {
        if (username.isEmpty()) return "Username cannot be empty";
        if (password.isEmpty()) return "Password cannot be empty";
        if (!password.equals(confirmPassword)) return "Passwords do not match";
        if (email.isEmpty()) return "Email cannot be empty";
        if (phoneNumber.isEmpty()) return "Phone number cannot be empty";
        return null; // No errors
    }

    private void showAlert(String message, Alert.AlertType type) {
        // In real usage, this shows an alert
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }



    @FXML
    protected void onShowUsersButtonClick() {
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Phone Number: " + user.getPhoneNumber());
            System.out.println("----");
        }
    }

    @FXML
    protected void onBackToLoginClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }
    @FXML
    protected void clearFocus() {
        Platform.runLater(() -> registerButton.getScene().getRoot().requestFocus());
    }
}
