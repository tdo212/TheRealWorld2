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
 * Controller for handling profile-related UI interactions and logic.
 */
public class ProfileController {

    @FXML
    private Label profileNameLabel; // Label to display the user's profile name
    @FXML
    private Label profileEmailLabel; // Label to display the user's profile email
    @FXML
    private Label profilePhoneNumberLabel; // Label to display the user's profile phone number

    private SqliteDAO databaseObject = new SqliteDAO(); // Database access object for fetching profile data
    int userId = UserSession.getInstance().getUserId(); // Retrieve the user ID from the current session

    /**
     * Initializes the controller by displaying profile details when the view is loaded.
     */
    public void initialize(){
        displayProfileDetails(userId);
    }

    /**
     * Retrieves and displays profile details for a given user ID.
     *
     * @param userId The ID of the user whose profile details are to be displayed.
     *               If the profile exists, it updates the profile labels with user data.
     *               Otherwise, logs an error message to the console.
     */
    public void displayProfileDetails(int userId) {
        UserProfile userProfile = databaseObject.fetchProfileDetails(userId); // Fetch profile details for the user

        if (userProfile != null) {
            // Set profile details to the UI labels
            profileNameLabel.setText(userProfile.getUsername());
            profileEmailLabel.setText(userProfile.getEmail());
            profilePhoneNumberLabel.setText(userProfile.getPhoneNumber());
        } else {
            System.out.println("No profile found for user ID: " + userId);
        }
    }

    /**
     * Handles the logoff button click event by redirecting the user to the login view.
     *
     * @param event The action event triggered by the logoff button click.
     * @throws IOException If an error occurs while loading the login view.
     */
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }
}
