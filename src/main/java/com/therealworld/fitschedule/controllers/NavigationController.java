package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.therealworld.fitschedule.model.UserSession;

import java.io.IOException;

/**
 * Controller class responsible for handling navigation between different views in the FitSchedule application.
 * Provides methods to switch between home, schedule, goals, and profile views.
 */
public class NavigationController {

    /**
     * Handles the navigation to the home (dashboard) view.
     * This method is triggered when the home navigation button is clicked.
     *
     * @param event the ActionEvent triggered by clicking the home navigation button.
     * @throws IOException if there is an error loading the FXML file for the dashboard view.
     */
    public void onHomeNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles the navigation to the schedule view.
     * This method is triggered when the schedule navigation button is clicked.
     *
     * @param event the ActionEvent triggered by clicking the schedule navigation button.
     * @throws IOException if there is an error loading the FXML file for the scheduler view.
     */
    public void onScheduleNavButtonClick(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            System.out.println("Navigating to scheduler-view.fxml...");
            FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("scheduler-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
            stage.setScene(scene);
            stage.show();
            System.out.println("Scheduler view loaded successfully.");
        } catch (IOException e) {
            System.out.println("Failed to load scheduler-view.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Handles the navigation to the goals view.
     * This method is triggered when the goals navigation button is clicked.
     *
     * @param event the ActionEvent triggered by clicking the goals navigation button.
     * @throws IOException if there is an error loading the FXML file for the goals view.
     */
    public void onGoalNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("goals-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles the navigation to the profile view.
     * This method is triggered when the profile navigation button is clicked.
     *
     * @param event the ActionEvent triggered by clicking the profile navigation button.
     * @throws IOException if there is an error loading the FXML file for the profile view.
     */
    public void onProfileNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }
}
