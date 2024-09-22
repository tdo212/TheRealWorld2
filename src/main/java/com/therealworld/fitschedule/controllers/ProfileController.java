package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.SqliteDAO;
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
    private Label profileHeightLabel;

    @FXML
    private Label profileWeightLabel;

    @FXML
    private Label profileTrainingFrequency;

    @FXML
    private Label profileCreationDate;

    @FXML
    private Label profileTrainingTimePreferences;


//    INSERT INTO userProfile VALUES (1,1,"JIM JOHNSON","BILBOBAGGINS32@gmail.com",1,1,1,1)

    @FXML
    public void displayProfileDetails(){
//        String userNameLabel = sqliteDAO.fetchProfileDetails();
//        profileNameLabel.setText(userNameLabel);
//        String emailLabel = sqliteDAO.fetchProfileDetails();
//        profileEmailLabel.setText(emailLabel);
//        String heightLabel = sqliteDAO.fetchProfileDetails();
//        profileHeightLabel.setText(heightLabel);
//        String weightLabel = sqliteDAO.fetchProfileDetails();
//        profileWeightLabel.setText(weightLabel);
//        String trainingFrequencyLabel = sqliteDAO.fetchProfileDetails();
//        profileTrainingFrequency.setText(trainingFrequencyLabel);
//        String profileCreationDateLabel = sqliteDAO.fetchProfileDetails();
//        profileCreationDate.setText(profileCreationDateLabel);
//        String profileTrainingTimeLabel = sqliteDAO.fetchProfileDetails();
//        profileTrainingTimePreferences.setText(profileTrainingTimeLabel);


    }

    @FXML
    public void initialize() {
        displayProfileDetails();
    }

    @FXML
    public void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    public void onProfileButtonClick(ActionEvent event) {

    }

    @FXML
    public void onEditDetailsButtonClick(ActionEvent event) {
    }

    @FXML
    public void onChangePasswordButtonClick(ActionEvent event) {
    }
}
