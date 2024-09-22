package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.UserSession;
import com.therealworld.fitschedule.FitScheduleApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;



public class DashboardController {

    private int userId;

//    Attempt at creating an Alert for Querying if User has trained for the day
    @FXML
    protected void alertHaveYouTrainedToday(ActionEvent event){

        Alert trainingEnquiry = new Alert(Alert.AlertType.CONFIRMATION);
        trainingEnquiry.setTitle("Have you trained today?");
        trainingEnquiry.setContentText("Test");
        trainingEnquiry.showAndWait();

    }


    @FXML
    protected void onLogoutButtonClick(ActionEvent event) throws IOException {
        // Load the login view when the user clicks the "Log Out" button
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }
    @FXML
    protected void onHomeButtonClick(ActionEvent event) throws IOException{

    }

    // Example method to load data for the dashboard
    private void loadDashboardData() {
        // Logic to load user-specific data based on the userId
        System.out.println("Loading dashboard data for user ID: " + userId);
    }

    public void onHomeNavButtonClick(ActionEvent event) throws IOException
    {
        // Load the dashboard view when the user clicks the "Home" button
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onScheduleNavButtonClick(ActionEvent event) throws IOException {

        // Load the schedule view when the user clicks the "Schedule" button
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("scheduler-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);

    }

    public void onGoalNavButtonClick(ActionEvent event) throws IOException {

        // Load the goals view when the user clicks the "Goals" button
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("goals-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onProfileNavButtonClick(ActionEvent event) throws IOException {

        // Load the profile view when the user clicks the "Profile" button
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onYesButtonClick(ActionEvent event) {
    }

    public void onNoButtonClick(ActionEvent event) {
    }

    // Method to set the user ID and load the schedule
    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("set user ID " +userId);
    }

    // Logoff button action
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    // May possible need to edit database to add date and time and then retrieve
    // the current day's date for showing 'Today's Schedule'

}
