package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SessionManager;
import com.therealworld.fitschedule.model.SqliteDAO;
import com.therealworld.fitschedule.model.UserProfile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
public class ProfileController {

    private SqliteDAO sqliteDAO = new SqliteDAO();

    @FXML
    private Label profileNameLabel;
    @FXML
    private Label profileEmailLabel;
    @FXML
    private Label profileTrainingFrequency;
    @FXML
    private Label profileCreationDate;
    @FXML
    private Label profileTrainingTimePreferences;

    public void setUserId(int userId) {
        displayProfileDetails(userId);
    }

    public void displayProfileDetails(int userId) {
        // Fetch profile details for the current user
        UserProfile userProfile = sqliteDAO.fetchProfileDetails(userId);

        if (userProfile != null) {
            // Set profile details to the UI labels
            profileNameLabel.setText(userProfile.getUsername());
            profileEmailLabel.setText(userProfile.getEmail());
            profileTrainingFrequency.setText(userProfile.getTrainingFrequency());
            profileCreationDate.setText(userProfile.getAccountCreationDate());
            profileTrainingTimePreferences.setText(userProfile.getPreferredTrainingTime());
        } else {
            System.out.println("No profile found for user ID: " + userId);
        }
    }
}
