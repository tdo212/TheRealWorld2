package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for the Dashboard view. Manages the display of today's schedule and handles navigation and user interaction events.
 */
public class DashboardController {

    @FXML
    private TableView<TodayScheduleRow> todaySchedule;  // TableView for displaying today's schedule

    @FXML
    private TableColumn<TodayScheduleRow, String> timeSlotColumn;  // Column displaying time slots

    @FXML
    private TableColumn<TodayScheduleRow, String> eventColumn;  // Column displaying events for each time slot

    private SqliteDAO scheduleDAO = new SqliteDAO();  // Data access object for interacting with the database

    private DayTracker dayTracker = new DayTracker();  // Utility class for managing day-related operations

    // Predefined time slots for a 24-hour schedule display
    private final String[] timeSlots = {
            "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM",
            "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
            "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
    };

    /**
     * Populates the `todaySchedule` TableView with the user's schedule for the current day.
     *
     * @param userId the ID of the user whose schedule is to be displayed.
     */
    private void populateTodaySchedule(int userId) {
        // Fetch all schedules for the user from the currentSchedule table
        List<Schedule> allSchedules = scheduleDAO.getScheduleForUser(userId);

        // Get today's day of the week (e.g., "Monday", "Tuesday", etc.)
        String currentDay = dayTracker.getCurrentDay();

        // Filter schedules that match today's day
        List<Schedule> todaySchedules = dayTracker.getEventsForToday(allSchedules);

        // Create a map to store events by time slot
        Map<String, String> eventsMap = new HashMap<>();

        // Fill the map with today's events based on their time slots
        for (Schedule event : todaySchedules) {
            eventsMap.put(event.getEventStartTime(), event.getEventName());
        }

        // Observable list to store the schedule rows for today
        ObservableList<TodayScheduleRow> scheduleRows = FXCollections.observableArrayList();

        // Iterate over the predefined time slots and fill in the data for today's events
        for (String timeSlot : timeSlots) {
            // Get the event for the time slot, otherwise leave it blank
            String eventName = eventsMap.getOrDefault(timeSlot, "");
            // Add a row for the current time slot and event
            scheduleRows.add(new TodayScheduleRow(timeSlot, eventName));
        }

        // Bind the TableView columns to the properties in TodayScheduleRow
        timeSlotColumn.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        eventColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));

        // Set the schedule rows into the TableView to display them
        todaySchedule.setItems(scheduleRows);
    }

    /**
     * Initializes the DashboardController by populating the TableView with the user's schedule for the current day.
     */
    @FXML
    public void initialize() {
        int userId = UserSession.getInstance().getUserId();  // Retrieve the logged-in user's ID
        populateTodaySchedule(userId);  // Populate the TableView with the user's schedule for today
    }

    /**
     * Displays an alert asking the user if they have trained today.
     *
     * @param event the ActionEvent triggered by the user.
     */
    @FXML
    protected void alertHaveYouTrainedToday(ActionEvent event) {
        Alert trainingEnquiry = new Alert(Alert.AlertType.CONFIRMATION);
        trainingEnquiry.setTitle("Have you trained today?");
        trainingEnquiry.setContentText("Test");
        trainingEnquiry.showAndWait();
    }

    /**
     * Logs out the user and navigates back to the login screen.
     *
     * @param event the ActionEvent triggered by the user.
     * @throws IOException if the FXML file cannot be loaded.
     */
    @FXML
    protected void onLogoutButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles the navigation to the Home view.
     *
     * @param event the ActionEvent triggered by the user.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public void onHomeNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles the navigation to the Schedule view.
     *
     * @param event the ActionEvent triggered by the user.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public void onScheduleNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("scheduler-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles the navigation to the Goals view.
     *
     * @param event the ActionEvent triggered by the user.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public void onGoalNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("goals-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles the navigation to the Profile view.
     *
     * @param event the ActionEvent triggered by the user.
     * @throws IOException if the FXML file cannot be loaded.
     */
    public void onProfileNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Logs the user off and navigates to the login screen.
     *
     * @param event the ActionEvent triggered by the user.
     * @throws IOException if the FXML file cannot be loaded.
     */
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Action method for handling the "Yes" button click event.
     *
     * @param event the ActionEvent triggered by the user.
     */
    @FXML
    public void onYesButtonClick(ActionEvent event) {
        System.out.println("Yes button clicked.");
    }

    /**
     * Action method for handling the "No" button click event.
     *
     * @param event the ActionEvent triggered by the user.
     */
    @FXML
    public void onNoButtonClick(ActionEvent event) {
        System.out.println("No button clicked.");
    }
}
