package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.*;
import com.therealworld.fitschedule.model.SqliteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private DayTracker dayTracker = new DayTracker();
    private ObservableList<Goal> goals;

    // Predefined time slots (24-hour format for simplicity)
    private final String[] timeSlots = {
            "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM",
            "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
            "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
    };

    // Populate today's schedule
    private void populateTodaySchedule(int userId) {
        try {
            // Fetch all schedules for the user for today
            String dayOfWeek = dayTracker.getCurrentDay();

            List<Schedule> todaySchedules = scheduleDAO.getCommitmentsForDay(userId, dayOfWeek);  // Directly fetch today's schedules

            // Create a map to store events by time slot
            Map<String, String> eventsMap = new HashMap<>();

            // Fill the map with today's events based on their time slots
            for (Schedule event : todaySchedules) {
                eventsMap.put(event.getEventStartTime(), event.getEventName()); // Use start time as the key
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

        } catch (Exception ex) {
            // Handle any exceptions that occur while populating today's schedule
            System.err.println("Error populating today's schedule: " + ex.getMessage());
            ex.printStackTrace();
        }
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
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onHomeNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onScheduleNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("scheduler-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onGoalNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("goals-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    public void onProfileNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("profile-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }
    int userId = UserSession.getInstance().getUserId();
    public void updatePieChart() {
        int goalsCompleted = scheduleDAO.getCompletedGoalsCount(userId);
        int goalsRemaining = scheduleDAO.countGoalsRemaining(userId);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("COmpleted Goals", goalsCompleted),
                new PieChart.Data("Incomplete Goals", goalsRemaining)
        );
        goalsPieChart.setData(pieChartData);
    }



    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }


    // Action method for "Yes" button
    @FXML
    public void onYesButtonClick(ActionEvent event) {
        System.out.println("Yes button clicked.");

        // Get the current day of the week
        String dayOfWeek = dayTracker.getCurrentDay();
        dayOfWeek = capitalizeFirstLetter(dayOfWeek); // Normalize the format to match the database columns

        int userId = UserSession.getInstance().getUserId();

        // Initialize the goals list before using it
        goals = SqliteDAO.getAllGoals(userId);  // Fetch all goals for the current user from the database

        if (goals == null || goals.isEmpty()) {
            System.out.println("No goals found for the user.");
            return;  // Exit if there are no goals
        }

        // Fetch all events for the current day
        List<Schedule> todaysEvents = scheduleDAO.getCommitmentsForDay(userId, dayOfWeek);

        // Filter the list to get only workout (fitness) events (where isFitnessEvent = true)
        List<Schedule> workoutEvents = todaysEvents.stream()
                .filter(schedule -> schedule.isFitnessEvent())  // Filter events marked as fitness workouts
                .collect(Collectors.toList());

        // Check if the list of workout events is empty
        if (workoutEvents.isEmpty()) {
            // Print message if no workout events are found
            System.out.println("No fitness events found for today.");
        } else {
            // Initialize the workoutHours variable
            int workoutHours = 0;

            // Process the workout events (display them and add to workoutHours)
            for (Schedule workout : workoutEvents) {
                System.out.println("Workout Event: " + workout.getEventName() + " at " + workout.getEventStartTime());

                // Since each event is 1 hour, increment workoutHours by 1 for each workout event
                workoutHours++;
            }
            System.out.print("Hours spent working out: " + workoutHours);
            // Now iterate over the goals and update progress
            for (Goal goal : goals) {
                System.out.println("Checking goal period: " + goal.getGoalPeriod());
                // Check if the goal is days per week or hours based
                if (goal.getGoalPeriod().equalsIgnoreCase("Days per week")) {
                    int currentProgress = goal.getGoalProgress();
                    System.out.println("Goal progress before update (Days per week): " + currentProgress);
                    int newProgress = currentProgress + 1;  // Increment progress by 1 day
                    scheduleDAO.updateGoalProgress(goal.getGoalId(), newProgress);
                    System.out.println("Updated weekly goal progress for goal ID: " + goal.getGoalId() + " to " + newProgress);


                } else if (goal.getGoalPeriod().equalsIgnoreCase("Hours per week")) {
                    int currentProgress = goal.getGoalProgress();
                    System.out.println("Goal progress before update (Hours per week): " + currentProgress);
                    int newProgress = currentProgress + workoutHours;  // Increment by workout hours
                    scheduleDAO.updateGoalProgress(goal.getGoalId(), newProgress);
                    System.out.println("Updated hourly goal progress for goal ID: " + goal.getGoalId() + " to " + newProgress);
                }
            }

        }
    }
    // Action method for "No" button
    @FXML
    public void onNoButtonClick (ActionEvent event) {
        System.out.println("No button clicked.");

        // Get the current day of the week
        String dayOfWeek = dayTracker.getCurrentDay();
        dayOfWeek = capitalizeFirstLetter(dayOfWeek); // Normalize the format to match the database columns

        int userId = UserSession.getInstance().getUserId();

        // Fetch all events for the current day
        List<Schedule> todaysEvents = scheduleDAO.getCommitmentsForDay(userId, dayOfWeek);

        // Filter the list to get only workout (fitness) events (where isFitnessEvent = true)
        List<Schedule> workoutEvents = todaysEvents.stream()
                .filter(schedule -> schedule.isFitnessEvent())  // Filter events marked as fitness workouts
                .collect(Collectors.toList());

        // Check if the list of workout events is empty
        if (workoutEvents.isEmpty()) {
            // Print message if no workout events are found
            System.out.println("No fitness events found for today.");
        } else {
            // Initialize the workoutHours variable
            int workoutHours = 0;

            // Process the workout events (display them and add to workoutHours)
            for (Schedule workout : workoutEvents) {
                System.out.println("Workout Event: " + workout.getEventName() + " at " + workout.getEventStartTime());

                // Since each event is 1 hour, increment workoutHours by 1 for each workout event
                workoutHours++;
                System.out.print("Hours spent working out: " + workoutHours);
            }
        }
    }
    // Helper method to normalize the dayOfWeek string (capitalize first letter)
    private String capitalizeFirstLetter (String dayOfWeek){
        if (dayOfWeek == null || dayOfWeek.isEmpty()) {
            return dayOfWeek;
        }
        return dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
    }
}



