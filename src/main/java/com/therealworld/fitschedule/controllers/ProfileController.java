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
public class ProfileController {

    private SqliteDAO sqliteDAO = new SqliteDAO();

    @FXML
    private Label profileNameLabel;
    @FXML
    private Label profileEmailLabel;
    @FXML
    private Label profilePhoneNumberLabel;
    @FXML
    private Label profileTrainingFrequency;
    @FXML
    private Label profileCreationDate;
    @FXML
    private Label profileTrainingTimePreferences;
    private SqliteDAO databaseObject = new SqliteDAO();
    int userId = UserSession.getInstance().getUserId();


    public void initialize(){
        displayProfileDetails(userId);
    }

//    public void setUserId(int userId) {
//        displayProfileDetails(userId);
//    }



    public void displayProfileDetails(int userId) {
        UserProfile userProfile = databaseObject.fetchProfileDetails(userId);
        // Fetch profile details for the current user

        if (userProfile != null) {
            // Set profile details to the UI labels
            profileNameLabel.setText(userProfile.getUsername());
            profileEmailLabel.setText(userProfile.getEmail());
            profilePhoneNumberLabel.setText(userProfile.getPhoneNumber());
            profileTrainingFrequency.setText(userProfile.getTrainingFrequency());
            profileCreationDate.setText(userProfile.getAccountCreationDate());
            profileTrainingTimePreferences.setText(userProfile.getPreferredTrainingTime());
        } else {
            System.out.println("No profile found for user ID: " + userId);
        }
    }

    public void displayProfile(int userId) {

    }

    public void onChangePasswordButtonClick(ActionEvent event) {
    }

    public void onEditDetailsButtonClick(ActionEvent event) {
    }

    // Logoff button action
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onProfileButtonClick(ActionEvent event) {
    }
}
