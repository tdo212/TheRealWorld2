package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.Schedule;
import com.therealworld.fitschedule.model.SqliteDAO;
import com.therealworld.fitschedule.model.ScheduleRow;
import com.therealworld.fitschedule.model.UserSession;
import com.therealworld.fitschedule.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.io.IOException;

import static com.therealworld.fitschedule.model.DateUtil.getWeekStartDate;

/**
 * The SchedulerController class is responsible for managing and displaying the user's weekly schedule.
 * It handles the UI components for the scheduler view, including the table layout and column bindings.
 * It also retrieves schedule data from the database and populates the TableView with scheduled events.
 */
public class SchedulerController {

    private int currentWeekOffset = 0;

    @FXML
    private VBox rootContainer;  // Main container that will hold all UI elements

    @FXML
    private TableView<ScheduleRow> scheduleTable;  // Updated TableView to use ScheduleRow

    @FXML
    private TableColumn<ScheduleRow, String> timeSlotColumn;  // New column for time slots
    @FXML
    private TableColumn<ScheduleRow, String> mondayColumn;
    @FXML
    private TableColumn<ScheduleRow, String> tuesdayColumn;
    @FXML
    private TableColumn<ScheduleRow, String> wednesdayColumn;
    @FXML
    private TableColumn<ScheduleRow, String> thursdayColumn;
    @FXML
    private TableColumn<ScheduleRow, String> fridayColumn;
    @FXML
    private TableColumn<ScheduleRow, String> saturdayColumn;
    @FXML
    private TableColumn<ScheduleRow, String> sundayColumn;
    @FXML
    private CheckBox fitnessEventCheckBox;

    private int userId;

    private final SqliteDAO scheduleDAO = new SqliteDAO();

    // Predefined time slots
    private final List<String> timeSlots = Arrays.asList(
            "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM",
            "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM",
            "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM",
            "9:00 PM", "10:00 PM", "11:00 PM"
    );

    /**
     * Initializes the SchedulerController, binding columns, and populating the schedule table
     * for the current week. It retrieves the user's weekly schedule data from the database
     * and sets it in the TableView.
     */
    @FXML
    public void initialize() {
        String currentWeekStartDate = getWeekStartDate(currentWeekOffset);
        this.userId = UserSession.getInstance().getUserId();
        System.out.println("User ID in SchedulerController: " + userId);
        bindTableColumns();
        System.out.println("Current Week Start Date: " + getWeekStartDate(0));  // Current week
        System.out.println("Previous Week Start Date: " + getWeekStartDate(-1));  // Previous week
        System.out.println("Next Week Start Date: " + getWeekStartDate(1));  // Next week
        populateScheduleTable(userId, currentWeekStartDate);
    }

    /**
     * Binds each TableColumn in the TableView to the appropriate property of ScheduleRow.
     * This allows each day's column in the TableView to display the correct data from ScheduleRow.
     */
    private void bindTableColumns() {
        timeSlotColumn.setCellValueFactory(cellData -> cellData.getValue().timeSlotProperty());
        mondayColumn.setCellValueFactory(cellData -> cellData.getValue().mondayProperty());
        tuesdayColumn.setCellValueFactory(cellData -> cellData.getValue().tuesdayProperty());
        wednesdayColumn.setCellValueFactory(cellData -> cellData.getValue().wednesdayProperty());
        thursdayColumn.setCellValueFactory(cellData -> cellData.getValue().thursdayProperty());
        fridayColumn.setCellValueFactory(cellData -> cellData.getValue().fridayProperty());
        saturdayColumn.setCellValueFactory(cellData -> cellData.getValue().saturdayProperty());
        sundayColumn.setCellValueFactory(cellData -> cellData.getValue().sundayProperty());
    }

    /**
     * Populates the schedule table for a specified user and week start date. It retrieves
     * the user's weekly schedule data from the database, merges it with predefined time slots,
     * and displays the combined data in the TableView.
     *
     * <p>Steps in this method include:
     * <ul>
     *   <li>Retrieves the weekly schedule for the specified user and week start date.</li>
     *   <li>Maps each time slot to its respective events.</li>
     *   <li>Creates an observable list of ScheduleRow objects for display in the TableView.</li>
     * </ul>
     *
     * @param userId              The ID of the user whose schedule is to be displayed.
     * @param currentWeekStartDate The start date of the week for which the schedule is being populated.
     */
    private void populateScheduleTable(int userId, String currentWeekStartDate) {
        List<String> timeSlots = Arrays.asList(
                "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM",
                "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
                "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
        );

        // Retrieve the weekly schedule data for the specified user and week
        List<String[]> scheduleData = scheduleDAO.getWeeklyScheduleForWeek(userId, currentWeekStartDate);

        Map<String, String[]> scheduleMap = new HashMap<>();
        if (scheduleData != null && !scheduleData.isEmpty()) {
            for (String[] row : scheduleData) {
                scheduleMap.put(row[0], row);  // Time slot as the key
                System.out.println("Added time slot: " + row[0] + " | Events: " + Arrays.toString(row));
            }
        } else {
            System.out.println("No schedule data found for this week.");
        }

        // Initialize the observable list for the table rows
        ObservableList<ScheduleRow> scheduleRows = FXCollections.observableArrayList();

        // Populate the table with predefined time slots and merge with any existing events for the selected week
        for (String timeSlot : timeSlots) {
            String[] eventRow = scheduleMap.getOrDefault(timeSlot, new String[8]);

            scheduleRows.add(new ScheduleRow(
                    timeSlot, // Always add the predefined time slot
                    eventRow[1] == null ? "" : eventRow[1],  // Monday's event
                    eventRow[2] == null ? "" : eventRow[2],  // Tuesday's event
                    eventRow[3] == null ? "" : eventRow[3],  // Wednesday's event
                    eventRow[4] == null ? "" : eventRow[4],  // Thursday's event
                    eventRow[5] == null ? "" : eventRow[5],  // Friday's event
                    eventRow[6] == null ? "" : eventRow[6],  // Saturday's event
                    eventRow[7] == null ? "" : eventRow[7]   // Sunday's event
            ));
        }

        // Bind the scheduleRows data to the TableView
        scheduleTable.setItems(scheduleRows);
        scheduleTable.refresh();  // Refresh the table view to reflect the new data
    }


    /**
     * Handles the action triggered by the "Logoff" button click. This method logs the user off by
     * navigating to the login view, effectively ending the user's session and returning them
     * to the login screen.
     *
     * @param event The action event triggered by the "Logoff" button click.
     * @throws IOException If an error occurs while loading the login view FXML file.
     */
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Handles the action triggered by the "Schedule" button click. This method updates the schedule
     * table view with the user's schedule for the currently selected week.
     */
    @FXML
    public void onScheduleButtonClick() {
        String currentWeekStartDate = getWeekStartDate(currentWeekOffset);
        int userId = UserSession.getInstance().getUserId();
        System.out.println("User ID in SchedulerController: " + userId);
        populateScheduleTable(userId, currentWeekStartDate);
    }

    /**
     * Handles the action triggered by the "Previous Week" button click. This method adjusts
     * the week offset to display the previous week's schedule in the TableView. It ensures
     * the weekly schedule table for the selected week exists, creating it if necessary,
     * and then populates the TableView with the previous week's data.
     */
    @FXML
    protected void onPreviousWeekButtonClick() {
        currentWeekOffset--;  // Move to the previous week
        String currentWeekStartDate = getWeekStartDate(currentWeekOffset);
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");

        checkAndCreateWeeklyTableIfNotExists(tableName, currentWeekStartDate);
        populateScheduleTable(userId, currentWeekStartDate);
    }

    /**
     * Handles the action triggered by the "Next Week" button click. This method adjusts
     * the week offset to display the next week's schedule in the TableView. It ensures
     * the weekly schedule table for the selected week exists, creating it if necessary,
     * and then populates the TableView with the next week's data.
     */
    @FXML
    protected void onNextWeekButtonClick() {
        currentWeekOffset++;  // Move to the next week
        String currentWeekStartDate = getWeekStartDate(currentWeekOffset);
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");

        checkAndCreateWeeklyTableIfNotExists(tableName, currentWeekStartDate);
        populateScheduleTable(userId, currentWeekStartDate);
    }

    /**
     * Handles the action triggered by the "Current Week" button click. This method resets the
     * week offset to the current week and displays the current week's schedule in the TableView.
     * It ensures the weekly schedule table for the selected week exists, creating it if necessary,
     * and then populates the TableView with the current week's data.
     */
    @FXML
    protected void onCurrentWeekButtonClick() {
        currentWeekOffset = 0;  // Reset to the current week
        String currentWeekStartDate = getWeekStartDate(currentWeekOffset);
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");

        checkAndCreateWeeklyTableIfNotExists(tableName, currentWeekStartDate);
        populateScheduleTable(userId, currentWeekStartDate);
    }




    /**
     * Handles the action triggered by the "Clear Schedule" button click. This method displays
     * a confirmation alert to the user, prompting them to confirm if they wish to clear their
     * schedule for the current week. If confirmed, the schedule is cleared from both the TableView
     * and the database for the user's current week.
     */
    @FXML
    protected void onChangeScheduleButtonClick() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Clear Schedule Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to clear the schedule?");
        confirmationAlert.setContentText("This action cannot be undone.");

        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirmationAlert.getButtonTypes().setAll(buttonYes, buttonNo);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                scheduleTable.getItems().clear();

                String currentWeekStartDate = getWeekStartDate(currentWeekOffset);
                int userId = UserSession.getInstance().getUserId();

                scheduleDAO.clearScheduleForUser(userId, currentWeekStartDate);
                showAlert("Success", "The schedule has been cleared.", Alert.AlertType.INFORMATION);
            }
        });
    }

    /**
     * Handles the action triggered by the "Generate New Schedule" button click. This method
     * clears the current layout and displays a form for the user to create a new commitment.
     * The form includes fields for day selection, start and end times, event name, description,
     * and a checkbox for marking the event as a fitness event. Upon submission, the event is
     * validated and then added to the database for the specified week.
     */
    @FXML
    public void onGenerateNewScheduleButtonClick() {
        try {
            String currentWeekStartDate = getWeekStartDate(currentWeekOffset);

            rootContainer.getChildren().clear();
            Label formTitle = new Label("Add a New Commitment For Week Start Date: " + currentWeekStartDate);

            ComboBox<String> dayComboBox = new ComboBox<>(FXCollections.observableArrayList(
                    "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
            ComboBox<String> timeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(timeSlots));
            ComboBox<String> endTimeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(timeSlots));

            TextField eventNameTextField = new TextField();
            eventNameTextField.setPromptText("Enter Event Name");

            TextField eventDescriptionTextField = new TextField();
            eventDescriptionTextField.setPromptText("Enter Event Description");

            fitnessEventCheckBox = new CheckBox("Is this a fitness event?");

            Button submitButton = new Button("Submit");
            submitButton.setOnAction(event -> {
                try {
                    String dayOfWeek = dayComboBox.getValue();
                    String startTime = cleanTimeString(timeSlotComboBox.getValue());
                    String endTime = cleanTimeString(endTimeSlotComboBox.getValue());
                    String eventDescription = eventDescriptionTextField.getText();
                    String eventName = eventNameTextField.getText();
                    boolean isFitnessEvent = fitnessEventCheckBox.isSelected();
                    int userId = UserSession.getInstance().getUserId();

                    if (dayOfWeek == null || startTime.isEmpty() || endTime.isEmpty() ||
                            eventDescription.isEmpty() || eventName.isEmpty()) {
                        showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
                        return;
                    }

                    if (!isValidTimeRange(startTime, endTime)) {
                        showAlert("Error", "End time must be after start time.", Alert.AlertType.ERROR);
                        return;
                    }

                    List<String> timeSlotsInRange = getTimeSlotsInRange(startTime, endTime);
                    for (String timeSlot : timeSlotsInRange) {
                        scheduleDAO.insertWeeklyEvent(userId, timeSlot, dayOfWeek, eventDescription,
                                getWeekStartDate(currentWeekOffset), isFitnessEvent);
                        if (isFitnessEvent) {
                            scheduleDAO.insertIntoFitnessEvents(userId, eventName, dayOfWeek, timeSlot, currentWeekStartDate);
                        }
                    }

                    showAlert("Success", "Event added successfully!", Alert.AlertType.INFORMATION);
                    populateScheduleTable(userId, currentWeekStartDate);
                    buildInitialLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "An error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            Button backButton = new Button("Back");
            backButton.setOnAction(event -> buildInitialLayout());

            rootContainer.getChildren().addAll(
                    formTitle,
                    new HBox(new Label("Day of the Week: "), dayComboBox),
                    new HBox(new Label("Start Time: "), timeSlotComboBox),
                    new HBox(new Label("End Time: "), endTimeSlotComboBox),
                    new HBox(new Label("Event Name: "), eventNameTextField),
                    new HBox(new Label("Description: "), eventDescriptionTextField),
                    fitnessEventCheckBox,
                    submitButton,
                    backButton
            );
        } catch (Exception e) {
            showAlert("Error", "An error occurred while generating the new schedule: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }




    /**
     * Handles the action triggered by the "Update Schedule" button click. This method displays a form
     * that allows the user to update an existing commitment. The form includes dropdowns for day selection,
     * start and end times, event selection, event name, and description. A fitness event checkbox is also
     * provided to mark the event type. Upon submission, the selected commitment's details are updated
     * in the database.
     */
    @FXML
    protected void onUpdateScheduleButtonClick() {
        try {
            String currentWeekStartDate = getWeekStartDate(currentWeekOffset);
            rootContainer.getChildren().clear();
            Label formTitle = new Label("Update an Existing Commitment");

            ComboBox<String> dayComboBox = new ComboBox<>(FXCollections.observableArrayList(
                    "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
            ComboBox<String> timeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(timeSlots));
            ComboBox<String> endTimeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(timeSlots));

            ComboBox<Schedule> eventComboBox = new ComboBox<>();
            CheckBox updateFitnessEventCheckBox = new CheckBox("Is this a fitness event?");

            dayComboBox.setOnAction(event -> {
                String selectedDay = dayComboBox.getValue();
                if (selectedDay != null) {
                    List<Schedule> commitments = scheduleDAO.getCommitmentsForDay(userId, selectedDay);
                    eventComboBox.setItems(FXCollections.observableArrayList(commitments));
                }
            });

            TextField eventNameTextField = new TextField();
            TextField eventDescriptionTextField = new TextField();

            eventComboBox.setOnAction(event -> {
                Schedule selectedSchedule = eventComboBox.getValue();
                if (selectedSchedule != null) {
                    eventNameTextField.setText(selectedSchedule.getEventName());
                    eventDescriptionTextField.setText(selectedSchedule.getEventDescription());
                    timeSlotComboBox.setValue(selectedSchedule.getEventStartTime());
                    updateFitnessEventCheckBox.setSelected(selectedSchedule.isFitnessEvent());
                }
            });

            Button submitButton = new Button("Submit");
            submitButton.setOnAction(event -> {
                try {
                    String dayOfWeek = dayComboBox.getValue();
                    String timeSlot = timeSlotComboBox.getValue();
                    String eventDescription = eventDescriptionTextField.getText();
                    boolean isFitnessEvent = updateFitnessEventCheckBox.isSelected();

                    int userId = UserSession.getInstance().getUserId();

                    if (dayOfWeek == null || timeSlot == null || eventDescription.isEmpty()) {
                        showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
                        return;
                    }

                    scheduleDAO.insertWeeklyEvent(userId, timeSlot, dayOfWeek, eventDescription,
                            currentWeekStartDate, isFitnessEvent);

                    showAlert("Success", "Commitment added successfully!", Alert.AlertType.INFORMATION);
                    populateScheduleTable(userId, currentWeekStartDate);
                    buildInitialLayout();
                } catch (Exception e) {
                    showAlert("Error", "An error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            Button backButton = new Button("Back");
            backButton.setOnAction(event -> buildInitialLayout());

            rootContainer.getChildren().addAll(
                    formTitle,
                    new HBox(new Label("Day of the Week: "), dayComboBox),
                    new HBox(new Label("Start Time: "), timeSlotComboBox),
                    new HBox(new Label("Event Name: "), eventNameTextField),
                    new HBox(new Label("Description: "), eventDescriptionTextField),
                    new HBox(new Label("Select Event: "), eventComboBox),
                    updateFitnessEventCheckBox,
                    submitButton,
                    backButton
            );
        } catch (Exception e) {
            showAlert("Error", "An error occurred while updating the schedule: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    /**
     * Loads and sets up the initial layout for the scheduler view. This method is used to reset
     * the layout to its original view after performing operations such as adding or updating events.
     */
    private void buildInitialLayout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("scheduler-view.fxml"));
            VBox scheduleLayout = fxmlLoader.load();
            rootContainer.getChildren().clear();
            rootContainer.getChildren().add(scheduleLayout);
        } catch (IOException e) {
            showAlert("Error", "Failed to load the schedule layout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Utility method to display an alert dialog with a specified title, message, and alert type.
     *
     * @param title      The title of the alert dialog.
     * @param content    The message to display in the alert dialog.
     * @param alertType  The type of alert to display (e.g., INFORMATION, ERROR).
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Checks if a weekly schedule table for a given start date exists in the database.
     * If the table does not exist, this method creates it. This ensures that each week's
     * schedule data is stored in a dedicated table.
     *
     * @param tableName       The name of the table to check.
     * @param weekStartDate   The start date of the week for which the table is checked or created.
     */
    private void checkAndCreateWeeklyTableIfNotExists(String tableName, String weekStartDate) {
        if (!scheduleDAO.doesTableExist(tableName)) {
            System.out.println("Table for the week starting on " + weekStartDate + " does not exist. Creating it now...");
            scheduleDAO.createWeeklyScheduleTable(weekStartDate, userId);
        } else {
            System.out.println("Table for the week starting on " + weekStartDate + " already exists.");
        }
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    /**
     * Validates that the end time is later than the start time.
     *
     * @param startTime The start time of the event in "h:mm a" format.
     * @param endTime   The end time of the event in "h:mm a" format.
     * @return          True if the end time is after the start time; otherwise, false.
     */
    private boolean isValidTimeRange(String startTime, String endTime) {
        try {
            System.out.println("Sanitized Start Time: " + startTime);
            System.out.println("Sanitized End Time: " + endTime);

            LocalTime start = LocalTime.parse(startTime, formatter);
            LocalTime end = LocalTime.parse(endTime, formatter);

            return end.isAfter(start);
        } catch (Exception e) {
            System.err.println("Error parsing time: " + e.getMessage());
            return false;
        }
    }



    /**
     * Generates a list of time slots within a specified range of start and end times.
     * This method increments the start time by one hour until it reaches the end time.
     *
     * @param startTime The start time of the range in "h:mm a" format.
     * @param endTime   The end time of the range in "h:mm a" format.
     * @return          A list of time slots within the specified time range.
     */
    private List<String> getTimeSlotsInRange(String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
        LocalTime start = LocalTime.parse(startTime, formatter);
        LocalTime end = LocalTime.parse(endTime, formatter);

        List<String> slotsInRange = new ArrayList<>();
        while (!start.isAfter(end)) {
            slotsInRange.add(start.format(formatter));  // Add the current time slot
            start = start.plusHours(1);  // Increment by 1 hour
        }
        return slotsInRange;
    }

    /**
     * Cleans a time string by removing any extra whitespace and replacing multiple spaces with a single space.
     *
     * @param time The time string to be cleaned.
     * @return     The cleaned time string.
     */
    private String cleanTimeString(String time) {
        return time.replaceAll("\\s+", " ").trim();
    }

}