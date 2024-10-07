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

/**
 * Controller class for handling user interactions with the login screen.
 * This class manages user authentication, navigation to the dashboard, and loading the application logo.
 */
public class LoginController {

    /**
     * TextField for the username input.
     */
    @FXML
    private TextField usernameField;

    /**
     * PasswordField for the password input.
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Button to initiate the login process.
     */
    @FXML
    private Button loginButton;

    /**
     * Button to navigate to the registration view.
     */
    @FXML
    private Button registerButton;

    /**
     * Button to cancel the login operation and close the application.
     */
    @FXML
    private Button cancelButton;

    /**
     * VBox container for managing focus and layout of the login view.
     */
    @FXML
    private VBox loginContainer;

    /**
     * ImageView to display the application logo on the login screen.
     */
    @FXML
    private ImageView logo;

    /**
     * DAO instance for interacting with the database.
     */
    private SqliteDAO userDAO = new SqliteDAO();

    /**
     * Initializes the login view by setting up the initial state, loading the logo image, and configuring UI behavior.
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
     * Handles the login button click event. Authenticates the user and, if successful,
     * navigates to the dashboard view.
     *
     * @throws IOException if the dashboard view cannot be loaded.
     */
    @FXML
    protected void onLoginButtonClick() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Authenticate the user
        boolean isAuthenticated = userDAO.authenticateUser(username, password);

        if (isAuthenticated) {
            System.out.println("Login successful");

            int userId = userDAO.getUserId(username);  // Get the user ID from the database
            UserSession.getInstance().setUserId(userId);  // Set the user ID globally
            System.out.println("User ID set in session: " + userId);

            SqliteDAO dao = new SqliteDAO();
            dao.createWeeklyScheduleTable(userId);  // Create the weekly schedule table for the user
            dao.populateTimeSlots(userId);  // Populate time slots for the user

            // Load and display the dashboard view
            FXMLLoader dashboardLoader = new FXMLLoader(FitScheduleApp.class.getResource("/com/therealworld/fitschedule/dashboard-view.fxml"));
            Parent dashboardRoot = dashboardLoader.load();  // Load the FXML file for the dashboard

            // Display the dashboard scene
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.show();
        } else {
            System.out.println("Login failed. Invalid username or password.");
            showAlert("Error", "Invalid username or password.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Displays an alert dialog with the given title, content, and alert type.
     *
     * @param title     the title of the alert dialog.
     * @param content   the content message of the alert dialog.
     * @param alertType the type of alert (e.g., error, information).
     */
    @FXML
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handles the register button click event. Navigates to the registration view.
     *
     * @throws IOException if the registration view cannot be loaded.
     */
    @FXML
    protected void onRegisterButtonClick() throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles the cancel button click event. Closes the current window.
     */
    @FXML
    protected void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
