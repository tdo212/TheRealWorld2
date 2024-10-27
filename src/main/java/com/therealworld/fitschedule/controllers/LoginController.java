package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.DateUtil;
import com.therealworld.fitschedule.model.UserSession;
import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.SqliteDAO;
import com.therealworld.fitschedule.controllers.DashboardController;
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

/**
 * Controller for managing login operations and user authentication.
 */
public class LoginController {
    @FXML
    TextField usernameField; // TextField for entering the username
    @FXML
    PasswordField passwordField; // PasswordField for entering the password
    @FXML
    Button loginButton; // Button to trigger login action
    @FXML
    private Button registerButton; // Button to navigate to registration view
    @FXML
    private Button cancelButton; // Button to cancel login and close the application
    @FXML
    private VBox loginContainer; // Container for login fields and buttons
    @FXML
    private ImageView logo; // ImageView to display the application logo

    private SqliteDAO userDAO = new SqliteDAO(); // DAO for interacting with the database

    /**
     * Initializes the login view by setting up focus management, logo image loading, and event handling.
     * This method is called automatically upon view loading.
     */
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

    /**
     * Handles the login button click event, authenticating the user with the provided credentials.
     * If successful, it initializes the session and loads the dashboard view.
     *
     * @throws IOException if loading the dashboard view fails.
     */
    @FXML
    protected void onLoginButtonClick() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if either the username or password field is empty
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showAlert("Error", "Username or Password cannot be empty.", Alert.AlertType.ERROR);
            return; // Stop further processing if fields are empty
        }

        // Authenticate the user
        boolean isAuthenticated = userDAO.authenticateUser(username, password);

        if (isAuthenticated) {
            System.out.println("Login successful");

            int userId = userDAO.getUserId(username); // Get the user ID from the database
            UserSession userSession = UserSession.getInstance();
            userSession.setUserId(userId); // Set the user ID globally

            System.out.println("User ID set in session: " + userId);

            SqliteDAO dao = new SqliteDAO();
            String weekStartDate = DateUtil.getWeekStartDate(0);
            dao.createWeeklyScheduleTable(weekStartDate, userId);
            dao.populateTimeSlots(userId, weekStartDate);

            // Load and display the dashboard view
            FXMLLoader dashboardLoader = new FXMLLoader(FitScheduleApp.class.getResource("/com/therealworld/fitschedule/dashboard-view.fxml"));
            Parent dashboardRoot = dashboardLoader.load(); // Load the FXML file for the dashboard

            // Display the dashboard scene
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot, FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT));
            stage.show();
        } else {
            System.out.println("Login failed. Invalid username or password.");
            showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Displays an alert dialog with the specified title, content, and alert type.
     *
     * @param title     The title of the alert dialog.
     * @param content   The message content of the alert dialog.
     * @param alertType The type of alert (e.g., error, information).
     */
    @FXML
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handles the register button click event, navigating the user to the registration view.
     *
     * @throws IOException if loading the registration view fails.
     */
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles the cancel button click event, closing the application.
     */
    @FXML
    protected void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}

