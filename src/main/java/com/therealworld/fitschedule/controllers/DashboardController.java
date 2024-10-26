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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;

import static com.therealworld.fitschedule.model.DateUtil.getWeekStartDate;

public class DashboardController {

    @FXML
    TableView<TodayScheduleRow> todaySchedule;  // TableView for today's schedule

    @FXML
    TableColumn<TodayScheduleRow, String> timeSlotColumn;  // The Time Slot column
    @FXML
    TableColumn<TodayScheduleRow, String> eventColumn;  // The Event column
    @FXML
    private PieChart goalsPieChart;
    @FXML
    SqliteDAO scheduleDAO = new SqliteDAO();
    DayTracker dayTracker = new DayTracker();
    private ObservableList<Goal> goals;

    public void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);  // Optional: remove header text for simplicity
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Predefined time slots (24-hour format for simplicity)
    final String[] timeSlots = {
            "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM",
            "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
            "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
    };

    // Populate today's schedule
    void populateTodaySchedule(int userId) {
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

    private void printNextDaySchedule(int userId) {
        try {
            // Determine the next day based on the current day
            String nextDay = getNextDay();

            // Fetch all schedules for the user for the next day
            List<Schedule> nextDaySchedules = scheduleDAO.getCommitmentsForDay(userId, nextDay);


            // Create a map of events by time slot for easy lookup
            Map<String, String> eventsMap = new HashMap<>();
            for (Schedule event : nextDaySchedules) {
                eventsMap.put(event.getEventStartTime(), event.getEventName());
            }

            // Print the schedule for the next day to the console
            System.out.println("Schedule for " + nextDay + ":");
            for (String timeSlot : timeSlots) {
                String eventName = eventsMap.getOrDefault(timeSlot, "Free");
                System.out.println(timeSlot + ": " + eventName);
            }
            calculateWorkoutHoursToday(userId);
        } catch (Exception ex) {
            System.err.println("Error printing next day's schedule: " + ex.getMessage());
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
                new PieChart.Data("Completed Goals", goalsCompleted),
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
        dayOfWeek = capitalizeFirstLetter(dayOfWeek); // Normalize format to match the database columns

        int userId = UserSession.getInstance().getUserId();

        // Initialize the goals list before using it
        goals = SqliteDAO.getAllGoals(userId);  // Fetch all goals for the current user from the database

        for (Goal goal : goals) {
            System.out.println("Fetched goal entry: " + goal);  // Print fetched goal data
        }

        if (goals == null || goals.isEmpty()) {
            System.out.println("No goals found for the user.");
            return;  // Exit if there are no goals
        }

        // Fetch all fitness events for the current day directly from the fitness_events table
        List<Schedule> workoutEvents = scheduleDAO.getFitnessEventsForDay(userId, dayOfWeek);

        // Check if the list of workout events is empty
        if (workoutEvents.isEmpty()) {
            System.out.println("No fitness events found for today.");
        } else {
            int workoutHours = workoutEvents.size(); // Count each event as 1 hour of workout

            // Print each workout event and calculate total hours
            for (Schedule workout : workoutEvents) {
                System.out.println("Workout Event: " + workout.getEventName() + " at " + workout.getEventStartTime());
            }
            System.out.println("Hours spent working out: " + workoutHours);

            // Iterate over the goals and update progress
            for (Goal goal : goals) {
                System.out.println("Checking goal period: " + goal.getGoalPeriod());

                if (goal.getGoalPeriod().equalsIgnoreCase("Days per week")) {
                    int currentProgress = goal.getGoalProgress();
                    int newProgress = currentProgress + 1;  // Increment progress by 1 day
                    scheduleDAO.updateGoalProgress(goal.getGoalId(), newProgress);
                    System.out.println("Updated weekly goal progress for goal ID: " + goal.getGoalId() + " to " + newProgress);

                    if (newProgress >= goal.getGoalDuration()) {
                        System.out.println("Goal with ID " + goal.getGoalId() + " has been completed.");
                        scheduleDAO.updateGoalAsCompleted(goal.getGoalId());
                        scheduleDAO.incrementTotalGoalsCompleted(userId);
                        scheduleDAO.awardBadge(userId, "Goal Completed: " + goal.getGoalType());
                    }

                } else if (goal.getGoalPeriod().equalsIgnoreCase("Hours per week")) {
                    int currentProgress = goal.getGoalProgress();
                    int newProgress = currentProgress + workoutHours;  // Increment by workout hours
                    scheduleDAO.updateGoalProgress(goal.getGoalId(), newProgress);
                    System.out.println("Updated hourly goal progress for goal ID: " + goal.getGoalId() + " to " + newProgress);

                    if (newProgress >= goal.getGoalDuration()) {
                        System.out.println("Goal with ID " + goal.getGoalId() + " has been completed.");
                        scheduleDAO.updateGoalAsCompleted(goal.getGoalId());
                        scheduleDAO.incrementTotalGoalsCompleted(userId);
                        scheduleDAO.awardBadge(userId, "Goal Completed: " + goal.getGoalType());
                    }
                }
            }
        }
    }


    // Action method for "No" button
    @FXML
    public void onNoButtonClick(ActionEvent event) {
        System.out.println("No button clicked.");
        int userId = UserSession.getInstance().getUserId();

        // Calculate workout hours needed for rescheduling
        int workoutHours = calculateWorkoutHoursToday(userId);

        // Find available workout slots for the following day
        List<String[]> availableSlots = findAvailableWorkoutSlots(userId, workoutHours);

        // Display the rescheduling options to the user
        showRescheduleAlert(userId, availableSlots, workoutHours);
    }

    // Helper method to normalize the dayOfWeek string (capitalize first letter)
    String capitalizeFirstLetter(String dayOfWeek){
        if (dayOfWeek == null || dayOfWeek.isEmpty()) {
            return dayOfWeek;
        }
        return dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
    }

    // Helper method to determine the next day of the week
    String getNextDay() {
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        DayOfWeek nextDay = currentDay.plus(1);  // Get the next day
        return capitalizeFirstLetter(nextDay.toString()); // Ensure formatting matches DB columns
    }

    int calculateWorkoutHoursToday(int userId) {
        // Get the current day
        String dayOfWeek = dayTracker.getCurrentDay();

        // Retrieve today's schedule for the user
        List<Schedule> todaySchedules = scheduleDAO.getCommitmentsForDay(userId, dayOfWeek);

        // Filter out only fitness events
        List<Schedule> workoutEvents = todaySchedules.stream()
                .filter(Schedule::isFitnessEvent)
                .collect(Collectors.toList());

        // Calculate the total duration of the workout events in hours
        int workoutHours = workoutEvents.size();

        System.out.println("Total workout hours for today: " + workoutHours);
        findAvailableWorkoutSlots(userId, workoutHours);
        return workoutHours;
    }

    private int currentOptionIndex = 0;  // Track the current option index

    private boolean workoutRescheduled = false;  // Flag to prevent duplicate scheduling

    List<String[]> findAvailableWorkoutSlots(int userId, int workoutHours) {
        List<String[]> availableSlots = new ArrayList<>();

        // Calculate the next day and retrieve its schedule
        String nextDay = getNextDay();
        List<Schedule> nextDaySchedules = scheduleDAO.getCommitmentsForDay(userId, nextDay);

        // Create a map of events by time slot for easy lookup
        Map<String, Boolean> occupiedSlots = new HashMap<>();
        for (Schedule event : nextDaySchedules) {
            occupiedSlots.put(event.getEventStartTime(), true);
        }

        // Iterate through time slots to find exactly `workoutHours` consecutive free slots
        for (int i = 0; i <= timeSlots.length - workoutHours; i++) {
            boolean consecutiveFree = true;

            // Check for `workoutHours` consecutive free slots
            for (int j = 0; j < workoutHours; j++) {
                String currentSlot = timeSlots[i + j];
                if (occupiedSlots.getOrDefault(currentSlot, false)) {
                    consecutiveFree = false;
                    break;
                }
            }

            // If we found exactly `workoutHours` consecutive free slots, add them as an available option
            if (consecutiveFree) {
                String[] freeSlots = Arrays.copyOfRange(timeSlots, i, i + workoutHours);
                availableSlots.add(freeSlots);
                i += workoutHours - 1;  // Skip ahead to prevent overlapping blocks
            }
        }

        // Output available slots for debugging
        System.out.println("Available workout slots in " + nextDay + ":");
        for (String[] slots : availableSlots) {
            System.out.println("Option: " + Arrays.toString(slots));
        }

        return availableSlots;
    }



    void showRescheduleAlert(int userId, List<String[]> availableSlots, int workoutHours) {
        if (availableSlots.isEmpty()) {
            System.out.println("No available time slots found for rescheduling.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reschedule Workout");
        alert.setHeaderText("Proposed Time Slot");

        currentOptionIndex = 0;  // Start with the first available option
        updateAlertContent(alert, availableSlots.get(currentOptionIndex));  // Show initial option

        ButtonType acceptButton = new ButtonType("Accept");
        ButtonType anotherTimeButton = new ButtonType("Find Another Time");
        ButtonType cancelButton = new ButtonType("Cancel Workout");

        alert.getButtonTypes().setAll(acceptButton, anotherTimeButton, cancelButton);

        while (true) {
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == acceptButton) {
                    // Accept the current option
                    String[] selectedSlot = availableSlots.get(currentOptionIndex);
                    insertAcceptedTimeForWorkout(userId, selectedSlot, getNextDay(), "Workout", "Rescheduled workout", true, getWeekStartDate(0));
                    workoutRescheduled = true;  // Mark workout as rescheduled
                    break;  // Exit the loop after accepting
                } else if (result.get() == anotherTimeButton) {
                    // Find the next available option
                    currentOptionIndex = (currentOptionIndex + 1) % availableSlots.size();
                    updateAlertContent(alert, availableSlots.get(currentOptionIndex));  // Update alert with next option
                } else if (result.get() == cancelButton) {
                    // Cancel the rescheduling
                    workoutRescheduled = true;
                    System.out.println("Workout reschedule cancelled.");
                    break;  // Exit loop without rescheduling
                }
            } else {
                // User closed the alert without selecting an option
                break;
            }
        }
    }

    // Helper to update the alert's content text with the current option's time slots
    private void updateAlertContent(Alert alert, String[] timeSlots) {
        alert.setContentText("Suggested time slot: " + String.join(", ", timeSlots));
    }

    //Insert rescheduled time into the weekly schedule table
    public void insertAcceptedTimeForWorkout(int userId, String[] acceptedTimeSlots, String dayOfWeek,
                                             String eventName, String eventDescription, boolean isFitnessEvent, String weekStartDate) {
        try {
            // Loop through each hour in the accepted time slot range and insert it as a scheduled event
            for (String timeSlot : acceptedTimeSlots) {
                // Insert the workout event into the database for the selected day and time slot
                scheduleDAO.insertWeeklyEvent(userId, timeSlot, dayOfWeek, eventDescription, weekStartDate, isFitnessEvent);
                // Insert the workout event into the fitness_events table
                scheduleDAO.insertIntoFitnessEvents(userId, eventName, dayOfWeek, timeSlot, weekStartDate);
            }

            System.out.println("Workout rescheduled successfully for " + dayOfWeek + " at times: " + Arrays.toString(acceptedTimeSlots));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while rescheduling the workout: " + e.getMessage(), Alert.AlertType.ERROR);
        }

    }

}



