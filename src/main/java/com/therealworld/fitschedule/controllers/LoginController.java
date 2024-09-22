package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.SqliteUserDAO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    protected TextField usernameField;
    @FXML
    protected PasswordField passwordField;
    @FXML
    Button loginButton;
    @FXML
    Button registerButton;
    @FXML
    Button cancelButton;
    @FXML
    private VBox loginContainer;

    @FXML
    private ImageView logo; // Add ImageView for the logo

    SqliteUserDAO userDAO = new SqliteUserDAO();

    @FXML
    public void initialize() {
        // Set focus on the VBox (or another container) after the scene is fully loaded
        Platform.runLater(() -> loginContainer.requestFocus());

        // Handle click on background to remove focus from text fields
        loginContainer.setOnMouseClicked(event -> loginContainer.requestFocus());

        // Load logo image programmatically
        Image logoImage = new Image(getClass().getResourceAsStream("/com/therealworld/fitschedule/images/logo.png"));

        if (logoImage != null) {
            logo.setImage(logoImage);
        } else {
            System.out.println("Logo image not found!");
        }
    }

    @FXML
    protected void onLoginButtonClick() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if the username or password fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Login failed. Username or password is empty.");
            showAlert("Error", "Username or password cannot be empty.", Alert.AlertType.ERROR);
            return;  // Exit the method if fields are empty
        }

        // Use SqliteUserDAO to authenticate the user
        boolean isAuthenticated = userDAO.authenticateUser(username, password);

        if (isAuthenticated) {
            System.out.println("Login successful");

            // Ensure the scene change happens on the JavaFX Application Thread
            Platform.runLater(() -> {
                try {
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("dashboard-view.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
                    stage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Login failed. Invalid username or password.");
            showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void onRegisterButtonClick() throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    protected void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        // Ensure the alert is shown on the JavaFX Application Thread
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
