package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.SqliteUserDAO;
import com.therealworld.fitschedule.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class RegisterController {

    private SqliteUserDAO userDAO;

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
    protected Button backToLoginButton; // Add this button reference

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

        if (validateRegistration(username, password, confirmPassword, email, phoneNumber)) {
            userDAO.addUser(username, password, email, phoneNumber);
            System.out.println("User registered successfully.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    private boolean validateRegistration(String username, String password, String confirmPassword, String email, String phoneNumber) {
        return !username.isEmpty() && !password.isEmpty() && password.equals(confirmPassword) && !email.isEmpty() && !phoneNumber.isEmpty();
    }

    @FXML
    protected void clearFocus() {
        // Use Platform.runLater to make sure the scene is ready before clearing focus
        Platform.runLater(() -> registerButton.getScene().getRoot().requestFocus());
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
}
