package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    public Button registerButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ImageView logo; // Add this for the logo ImageView

    @FXML

    protected void onLoginButtonClick() throws IOException {
        // Logic to authenticate the user
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authenticate(username, password)) {
            // If authentication is successful, load the main application window
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("main-view.fxml")); // Adjusted for main view
            Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
            stage.setScene(scene);
        } else {
            // Handle authentication failure (e.g., show an error message)
            System.out.println("Authentication failed.");
        }
    }

    @FXML
    protected void onCancelButtonClick() {
        // Close the application
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean authenticate(String username, String password) {
        // Simple authentication logic (replace with actual authentication)
        return "user".equals(username) && "pass".equals(password);
    }

    public void onRegisterButtonClick(ActionEvent actionEvent) {
        // Handle the register button click event
        System.out.println("Register button clicked.");
    }
}
