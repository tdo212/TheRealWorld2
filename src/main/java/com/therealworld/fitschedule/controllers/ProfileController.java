package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.SqliteDAO;
import com.therealworld.fitschedule.model.UserProfile;
import com.therealworld.fitschedule.model.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for handling the user profile view and related actions.
 */
public class ProfileController {

    // DAO to interact with the database
    private final SqliteDAO sqliteDAO = new SqliteDAO();

    // UI components linked to FXML labels for displaying username and email
    @FXML
    private Label profileNameLabel;
    @FXML
    private Label profileEmailLabel;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded and sets up the initial data.
     */
    @FXML
    public void initialize() {
        // Get the currently logged-in user ID
        int userId = UserSession.getInstance().getUserId();
        // Display profile details for the logged-in user
        displayProfileDetails(userId);
    }

    /**
     * Fetches and displays the profile details of the user on the UI.
     *
     * @param userId The ID of the user whose profile is to be displayed.
     */
    private void displayProfileDetails(int userId) {
        // Fetch profile details from the database for the current user
        UserProfile userProfile = sqliteDAO.fetchProfileDetails(userId);

        // If user profile is found, display the details in the corresponding labels
        if (userProfile != null) {
            profileNameLabel.setText(userProfile.getUsername());
            profileEmailLabel.setText(userProfile.getEmail());
        } else {
            System.out.println("No profile found for user ID: " + userId);
        }
    }

    /**
     * Handles the logoff button click event.
     * Logs off the user and redirects to the login view.
     *
     * @param event the event triggered by clicking the logoff button.
     * @throws IOException if there is an error loading the login-view FXML file.
     */
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles the profile button click event.
     * @param event the event triggered by clicking the profile button.
     */
    public void onProfileButtonClick(ActionEvent event) {
        // Already on the profile page, no action needed
    }
}
