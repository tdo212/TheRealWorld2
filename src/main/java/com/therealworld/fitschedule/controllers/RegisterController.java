package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.SqliteUserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
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

    private SqliteUserDAO userDAO = new SqliteUserDAO();  // Add the DAO for database operations

    @FXML
    protected void onRegisterButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();

        if (validateRegistration(username, password, confirmPassword, email, phoneNumber)) {
            // Add the user to the database using SqliteUserDAO
            userDAO.addUser(username, password, email, phoneNumber);
            System.out.println("User registered successfully.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    @FXML
    protected void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void onBackToLoginClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    private boolean validateRegistration(String username, String password, String confirmPassword, String email, String phoneNumber) {
        return !username.isEmpty() && !password.isEmpty() && password.equals(confirmPassword) && !email.isEmpty() && !phoneNumber.isEmpty();
    }

    @FXML
    protected void clearFocus() {
        // This will clear focus from all text fields when clicking outside
        registerButton.getScene().getRoot().requestFocus();
    }
}
