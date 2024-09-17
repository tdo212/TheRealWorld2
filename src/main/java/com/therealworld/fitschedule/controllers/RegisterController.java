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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();

        // Validate maximum username length (assuming the max length is 25 characters)
        if (username.length() > 25) {
            showAlert("Username exceeds maximum length of 25 characters", Alert.AlertType.ERROR);
            return;  // Stop further processing
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showAlert("Invalid email format", Alert.AlertType.ERROR);
            return;  // Stop further processing
        }

        // Validate phone number format
        if (!isValidPhoneNumber(phoneNumber)) {
            showAlert("Invalid phone number format", Alert.AlertType.ERROR);
            return;  // Stop further processing
        }

        // Validate password strength
        if (!isValidPassword(password)) {
            showAlert("Password must be at least 8 characters long and contain at least one digit, one uppercase letter, one lowercase letter, and one special character.", Alert.AlertType.ERROR);
            return;  // Stop further processing
        }

        // Collect empty fields
        List<String> emptyFields = getEmptyFields(username, password, confirmPassword, email, phoneNumber);

        if (!emptyFields.isEmpty()) {
            showAlert("The following fields are empty: " + String.join(", ", emptyFields), Alert.AlertType.ERROR);
        } else if (!password.equals(confirmPassword)) {
            showAlert("Passwords do not match", Alert.AlertType.ERROR);
        } else {
            // Proceed with registration if validation passes
            userDAO.addUser(username, password, email, phoneNumber);
            showAlert(REGISTRATION_SUCCESS, Alert.AlertType.INFORMATION);

            // Clear the input fields after successful registration
            clearInputFields();
        }
    }

    // Method to validate strong password
    private boolean isValidPassword(String password) {
        // Minimum 8 characters, at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
        return password.matches(passwordRegex);
    }

    // Method to collect empty fields
    private List<String> getEmptyFields(String username, String password, String confirmPassword, String email, String phoneNumber) {
        List<String> emptyFields = new ArrayList<>();

        if (username.isEmpty()) emptyFields.add("Username");
        if (password.isEmpty()) emptyFields.add("Password");
        if (confirmPassword.isEmpty()) emptyFields.add("Confirm Password");
        if (email.isEmpty()) emptyFields.add("Email");
        if (phoneNumber.isEmpty()) emptyFields.add("Phone Number");

        return emptyFields;
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

    // Method to validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; // Basic regex for email validation
        return email.matches(emailRegex);
    }

    // Method to validate phone number format
    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^[+]?[0-9]{10,15}$"; // Allows optional '+' and between 10 to 15 digits
        return phoneNumber.matches(phoneRegex);
    }
    // Method to clear all input fields
    private void clearInputFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        emailField.clear();
        phoneNumberField.clear();
    }
}
