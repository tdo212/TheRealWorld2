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

/**
 * Controller for the Dashboard view, handling the display of today's schedule,
 * navigation buttons, and goal-related features.
 */
public class DashboardController {

    @FXML
    TableView<TodayScheduleRow> todaySchedule;  // TableView for today's schedule

    @FXML
    TableColumn<TodayScheduleRow, String> timeSlotColumn;  // The Time Slot column
    @FXML
    TableColumn<TodayScheduleRow, String> eventColumn;  // The Event column

    @FXML
    private PieChart goalsPieChart;  // Pie chart displaying goal completion status

    @FXML
    SqliteDAO scheduleDAO = new SqliteDAO();  // DAO for interacting with schedule data

    DayTracker dayTracker = new DayTracker();  // Tracker for handling day-related operations

    private ObservableList<Goal> goals;  // List of user goals

    int userId = UserSession.getInstance().getUserId();  // Current user ID

    // Predefined time slots (24-hour format for simplicity)
    final String[] timeSlots = {
            "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM",
            "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
            "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
    };

    /**
     * Displays an alert with the specified title, message, and alert type.
     *
     * @param title     The title of the alert.
     * @param message   The message to display in the alert.
     * @param alertType The type of alert (e.g., INFORMATION, WARNING).
     */
    public void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);  // Optional: remove header text for simplicity
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Populates today's schedule for the user, retrieving events for each time slot
     * and displaying them in the TableView.
     *
     * @param userId The ID of the user whose schedule is to be populated.
     */
    void populateTodaySchedule(int userId) {
        try {
            String dayOfWeek = dayTracker.getCurrentDay();
            List<Schedule> todaySchedules = scheduleDAO.getCommitmentsForDay(userId, dayOfWeek);

            Map<String, String> eventsMap = new HashMap<>();
            for (Schedule event : todaySchedules) {
                eventsMap.put(event.getEventStartTime(), event.getEventName());
            }

            ObservableList<TodayScheduleRow> scheduleRows = FXCollections.observableArrayList();
            for (String timeSlot : timeSlots) {
                String eventName = eventsMap.getOrDefault(timeSlot, "");
                scheduleRows.add(new TodayScheduleRow(timeSlot, eventName));
            }

            timeSlotColumn.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
            eventColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
            todaySchedule.setItems(scheduleRows);

        } catch (Exception ex) {
            System.err.println("Error populating today's schedule: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Prints the next day's schedule for the user in the console.
     *
     * @param userId The ID of the user whose schedule is to be printed.
     */
    private void printNextDaySchedule(int userId) {
        try {
            String nextDay = getNextDay();
            List<Schedule> nextDaySchedules = scheduleDAO.getCommitmentsForDay(userId, nextDay);

            Map<String, String> eventsMap = new HashMap<>();
            for (Schedule event : nextDaySchedules) {
                eventsMap.put(event.getEventStartTime(), event.getEventName());
            }

            System.out.println("Schedule for " + nextDay + ":");
            for (String timeSlot : timeSlots) {
                String eventName = eventsMap.getOrDefault(timeSlot, "Free");
                System.out.println(timeSlot + ": " + eventName);
            }
        } catch (Exception ex) {
            System.err.println("Error printing next day's schedule: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Initializes the dashboard, populating today's schedule and updating the pie chart.
     */
    @FXML
    public void initialize() {
        populateTodaySchedule(userId);
        updatePieChart();
    }

    /**
     * Displays a confirmation alert asking if the user has trained today.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    protected void alertHaveYouTrainedToday(ActionEvent event) {
        Alert trainingEnquiry = new Alert(Alert.AlertType.CONFIRMATION);
        trainingEnquiry.setTitle("Have you trained today?");
        trainingEnquiry.setContentText("Test");
        trainingEnquiry.showAndWait();
    }

    /**
     * Logs out the user and redirects to the login view.
     *
     * @param event The action event triggered by the logout button click.
     * @throws IOException if the login view cannot be loaded.
     */
    @FXML
    protected void onLogoutButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Navigates to the home view of the application.
     *
     * @param event The action event triggered by the home navigation button click.
     * @throws IOException if the home view cannot be loaded.
     */
    public void onHomeNavButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    // Similar navigation methods for other views follow

    /**
     * Updates the goals pie chart with completed and incomplete goal counts.
     */
    public void updatePieChart() {
        int goalsCompleted = scheduleDAO.getCompletedGoalsCount(userId);
        int goalsRemaining = scheduleDAO.countGoalsRemaining(userId);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Completed Goals", goalsCompleted),
                new PieChart.Data("Incomplete Goals", goalsRemaining)
        );
        goalsPieChart.setData(pieChartData);
    }

    /**
     * Logs off the user and redirects to the login view.
     *
     * @param event The action event triggered by the logoff button click.
     * @throws IOException if the login view cannot be loaded.
     */
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }



    /**
     * Action method triggered when the "Yes" button is clicked. This method checks the user's fitness events
     * and updates their goals based on their workout activities for the current day. Specifically, it retrieves
     * the user's goals, identifies any fitness events for the current day, calculates the workout hours, and updates
     * the progress on goals with a period type of either "Days per week" or "Hours per week." If a goal is completed,
     * it marks the goal as completed, increments the user's total goals completed, and awards a badge for the goal type.
     *
     * @param event The action event triggered by the "Yes" button click.
     */
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



    /**
     * Action method triggered when the "No" button is clicked. This method calculates the workout hours
     * that need to be rescheduled, identifies available time slots for the next day, and displays rescheduling
     * options to the user based on their availability.
     *
     * <p>Specifically, it performs the following steps:
     * <ul>
     *   <li>Calculates the total workout hours for the current day.</li>
     *   <li>Finds consecutive time slots available on the following day for rescheduling.</li>
     *   <li>Displays an alert with available time slots for the user to select from.</li>
     * </ul>
     *
     * @param event The action event triggered by the "No" button click.
     */
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


    /**
     * Helper method to normalize the dayOfWeek string by capitalizing the first letter
     * and converting the rest to lowercase.
     *
     * @param dayOfWeek The name of the day of the week to be normalized.
     * @return The day of the week with the first letter capitalized and remaining letters in lowercase.
     *         Returns null if the input is null or an empty string if the input is empty.
     */
    String capitalizeFirstLetter(String dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek.isEmpty()) {
            return dayOfWeek;
        }
        return dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
    }

    /**
     * Helper method to determine the name of the next day of the week, formatted with the first letter
     * capitalized. This ensures the day name is in a consistent format suitable for database operations.
     *
     * @return The name of the next day with the first letter capitalized to match database formatting.
     */
    String getNextDay() {
        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        DayOfWeek nextDay = currentDay.plus(1);  // Get the next day
        return capitalizeFirstLetter(nextDay.toString()); // Ensure formatting matches DB columns
    }


    /**
     * Calculates the total workout hours for the current day by counting the fitness events
     * in the user's schedule. This method retrieves the user's schedule for today, filters out
     * non-fitness events, and counts each fitness event as one hour of workout time.
     *
     * <p>Specifically, it performs the following steps:
     * <ul>
     *   <li>Retrieves today's schedule for the user based on the current day.</li>
     *   <li>Filters the schedule to include only fitness-related events.</li>
     *   <li>Counts the total fitness events to calculate workout hours.</li>
     * </ul>
     *
     * @param userId The ID of the user whose workout hours are to be calculated.
     * @return The total number of hours spent working out today, calculated as the number of fitness events.
     */
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

    /**
     * Finds and returns a list of available workout time slots for the specified number of hours
     * on the next day. The method identifies consecutive free slots that match the required
     * duration for rescheduling a workout session.
     *
     * <p>Specifically, it performs the following steps:
     * <ul>
     *   <li>Calculates the next day and retrieves the user's schedule for that day.</li>
     *   <li>Creates a map of occupied slots for quick look-up of existing events.</li>
     *   <li>Searches for consecutive free slots that match the required workout hours.</li>
     *   <li>Adds each block of available time slots to the list of options.</li>
     * </ul>
     *
     * @param userId       The ID of the user whose schedule is being checked for availability.
     * @param workoutHours The number of consecutive hours required for the workout.
     * @return A list of available time slot arrays, where each array contains consecutive
     *         free time slots that match the required workout duration.
     */
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




    /**
     * Displays an alert dialog with options for rescheduling a workout. If available time slots are found,
     * the user can either accept a suggested time slot, find another time, or cancel the workout. The alert
     * shows the available workout slots sequentially, allowing the user to navigate through options.
     *
     * <p>Steps in this method include:
     * <ul>
     *   <li>If no available slots are found, it logs a message and exits.</li>
     *   <li>Displays the first available slot in a confirmation alert with options to accept, find another time, or cancel.</li>
     *   <li>If the user accepts, it saves the selected time slot for rescheduling.</li>
     *   <li>If the user opts to find another time, it shows the next available slot.</li>
     *   <li>If the user cancels, it exits without rescheduling.</li>
     * </ul>
     *
     * @param userId        The ID of the user for whom the workout is being rescheduled.
     * @param availableSlots A list of available time slots for the workout.
     * @param workoutHours  The number of consecutive hours required for the workout.
     */
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
                    String[] selectedSlot = availableSlots.get(currentOptionIndex);
                    insertAcceptedTimeForWorkout(userId, selectedSlot, getNextDay(), "Workout", "Rescheduled workout", true, getWeekStartDate(0));
                    workoutRescheduled = true;
                    break;
                } else if (result.get() == anotherTimeButton) {
                    currentOptionIndex = (currentOptionIndex + 1) % availableSlots.size();
                    updateAlertContent(alert, availableSlots.get(currentOptionIndex));
                } else if (result.get() == cancelButton) {
                    workoutRescheduled = true;
                    System.out.println("Workout reschedule cancelled.");
                    break;
                }
            } else {
                break;
            }
        }
    }

    /**
     * Updates the content text of an alert with the specified time slots, showing the suggested
     * workout rescheduling options.
     *
     * @param alert     The alert to be updated.
     * @param timeSlots The time slots to display in the alert's content text.
     */
    private void updateAlertContent(Alert alert, String[] timeSlots) {
        alert.setContentText("Suggested time slot: " + String.join(", ", timeSlots));
    }

    /**
     * Inserts the accepted rescheduled workout time slots into the user's weekly schedule and fitness events table.
     * This method iterates through each accepted time slot and adds it as a scheduled event in the database.
     *
     * @param userId          The ID of the user for whom the workout is being rescheduled.
     * @param acceptedTimeSlots An array of consecutive time slots for the rescheduled workout.
     * @param dayOfWeek       The day of the week for the rescheduled workout.
     * @param eventName       The name of the workout event.
     * @param eventDescription A description of the workout event.
     * @param isFitnessEvent  A boolean indicating if the event is a fitness-related activity.
     * @param weekStartDate   The start date of the week in which the workout is being rescheduled.
     */
    public void insertAcceptedTimeForWorkout(int userId, String[] acceptedTimeSlots, String dayOfWeek,
                                             String eventName, String eventDescription, boolean isFitnessEvent, String weekStartDate) {
        try {
            for (String timeSlot : acceptedTimeSlots) {
                scheduleDAO.insertWeeklyEvent(userId, timeSlot, dayOfWeek, eventDescription, weekStartDate, isFitnessEvent);
                scheduleDAO.insertIntoFitnessEvents(userId, eventName, dayOfWeek, timeSlot, weekStartDate);
            }

            System.out.println("Workout rescheduled successfully for " + dayOfWeek + " at times: " + Arrays.toString(acceptedTimeSlots));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while rescheduling the workout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}