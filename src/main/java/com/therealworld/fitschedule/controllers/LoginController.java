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

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Button cancelButton;
    @FXML
    private VBox loginContainer;

    @FXML
    private ImageView logo; // ImageView for the logo

    private SqliteDAO userDAO = new SqliteDAO();

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

        // Authenticate the user
        boolean isAuthenticated = userDAO.authenticateUser(username, password);

        if (isAuthenticated) {
            System.out.println("Login successful");

            int userId = userDAO.getUserId(username);  // Get the user ID from the database
            UserSession userSession = UserSession.getInstance();
            userSession.getInstance().setUserId(userId);  // Set the user ID globally
            System.out.println("User ID set in session: " + userId);


            SqliteDAO dao = new SqliteDAO();
            String weekStartDate = DateUtil.getWeekStartDate(0);
            dao.createWeeklyScheduleTable(weekStartDate, userId);
            dao.populateTimeSlots(userId);

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



    @FXML
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
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
}
