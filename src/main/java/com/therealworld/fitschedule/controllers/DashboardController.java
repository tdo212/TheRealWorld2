package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardController {

    @FXML
    private TableView<TodayScheduleRow> todaySchedule;  // TableView for today's schedule

    @FXML
    private TableColumn<TodayScheduleRow, String> timeSlotColumn;  // The Time Slot column
    @FXML
    private TableColumn<TodayScheduleRow, String> eventColumn;  // The Event column
    @FXML
    private PieChart goalsPieChart;
    @FXML

    private SqliteDAO scheduleDAO = new SqliteDAO();
    private DayTracker dayTracker = new DayTracker();  // Initialize the DayTracker

    // Predefined time slots (24-hour format for simplicity)
    private final String[] timeSlots = {
            "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM",
            "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
            "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
    };

    // Populate today's schedule
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

        // Predefined time slots
        String[] timeSlots = {
                "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM",
                "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
                "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
        };

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


    @FXML
    public void initialize() {
        int userId = UserSession.getInstance().getUserId();
        populateTodaySchedule(userId);
        updatePieChart();
    }

    @FXML
    protected void alertHaveYouTrainedToday(ActionEvent event) {
        Alert trainingEnquiry = new Alert(Alert.AlertType.CONFIRMATION);
        trainingEnquiry.setTitle("Have you trained today?");
        trainingEnquiry.setContentText("Test");
        trainingEnquiry.showAndWait();
    }

    @FXML
    protected void onLogoutButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onHomeNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onScheduleNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("scheduler-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onGoalNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("goals-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onProfileNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }
    int userId = UserSession.getInstance().getUserId();
    public void updatePieChart() {
        int goalsCompleted = scheduleDAO.getCompletedGoalsCount(userId);
        int goalsRemaining = scheduleDAO.countGoalsRemaining(userId);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Completed Goals", goalsCompleted),
                new PieChart.Data("Incomplete Goals", goalsRemaining)
        );
        goalsPieChart.setData(pieChartData);
    }



    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }
    // Action method for "Yes" button
    @FXML
    public void onYesButtonClick(ActionEvent event) {
        System.out.println("Yes button clicked.");
    }

    // Action method for "No" button
    @FXML
    public void onNoButtonClick(ActionEvent event) {
        System.out.println("No button clicked.");
    }


}
