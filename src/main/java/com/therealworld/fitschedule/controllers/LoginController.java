package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.UserSession;
import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.SqliteDAO;
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
import javafx.scene.Parent;

import java.io.IOException;

public class LoginController {

    @FXML
    protected TextField usernameField;
    @FXML
    protected PasswordField passwordField;
    @FXML
    protected Button loginButton;
    @FXML
    protected Button registerButton;
    @FXML
    protected Button cancelButton;
    @FXML
    protected VBox loginContainer;
    @FXML
    protected ImageView logo;

    SqliteDAO userDAO = new SqliteDAO();

    @FXML
    public void initialize() {
        // Set focus on the VBox after the scene is fully loaded
        Platform.runLater(() -> loginContainer.requestFocus());

        // Handle click on background to remove focus from text fields
        loginContainer.setOnMouseClicked(event -> loginContainer.requestFocus());

        // Load logo image programmatically
        Image logoImage = new Image(getClass().getResourceAsStream("/com/therealworld/fitschedule/logo.png"));

        if (logo == null) {
            System.out.println("Logo ImageView is null!");
        }
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

        // Check if username or password is empty
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Login failed. Username or password is empty.");
            showAlert("Error", "Please enter both username and password.", Alert.AlertType.ERROR);
            return;  // Return early to prevent authentication call
        }

        // Authenticate the user
        boolean isAuthenticated = userDAO.authenticateUser(username, password);

        if (isAuthenticated) {
            System.out.println("Login successful");

            int userId = userDAO.getUserId(username);  // Get the user ID from the database
            UserSession.getInstance().setUserId(userId);  // Set the user ID globally
            System.out.println("User ID set in session: " + userId);

            SqliteDAO dao = new SqliteDAO();
            dao.createWeeklyScheduleTable(userId);
            dao.populateTimeSlots(userId);

            // UI update must be done on the JavaFX Application Thread
            Platform.runLater(() -> {
                try {
                    // Load and display the dashboard view
                    FXMLLoader dashboardLoader = new FXMLLoader(FitScheduleApp.class.getResource("/com/therealworld/fitschedule/dashboard-view.fxml"));
                    Parent dashboardRoot = dashboardLoader.load();  // Load the FXML file for the dashboard

                    // Display the dashboard scene
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(new Scene(dashboardRoot));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Login failed. Invalid username or password.");
            // Use Platform.runLater to ensure UI changes happen on the JavaFX Application Thread
            Platform.runLater(() -> showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR));
        }
    }


    @FXML
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    @FXML
    protected void onRegisterButtonClick() throws IOException {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) registerButton.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("register-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    protected void onCancelButtonClick() {
        Platform.runLater(() -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
    }
}
