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

    @FXML
    public void initialize() {
        String currentWeekStartDate = getWeekStartDate(currentWeekOffset);
        // Access the userId from the global session
        this.userId = UserSession.getInstance().getUserId();
        System.out.println("User ID in SchedulerController: " + userId);
        // Bind the TableColumns to ScheduleRow properties
        bindTableColumns();
        System.out.println("Current Week Start Date: " + getWeekStartDate(0));  // Current week
        System.out.println("Previous Week Start Date: " + getWeekStartDate(-1));  // Previous week
        System.out.println("Next Week Start Date: " + getWeekStartDate(1));  // Next week
        populateScheduleTable(userId, currentWeekStartDate);
    }

    // Bind the TableColumns to ScheduleRow properties
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

    // Method to populate the schedule table for a specific user and week offset
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
                // Debugging log
                System.out.println("Added time slot: " + row[0] + " | Events: " + Arrays.toString(row));
            }
        } else {
            System.out.println("No schedule data found for this week.");
        }


        // Initialize the observable list for the table rows
        ObservableList<ScheduleRow> scheduleRows = FXCollections.observableArrayList();

        // Populate the table with predefined time slots and merge with any existing events for the selected week
        for (String timeSlot : timeSlots) {
            // Retrieve the schedule row for this time slot from the map
            String[] eventRow = scheduleMap.getOrDefault(timeSlot, new String[8]);

            // Add the row to the table, ensuring that each day (column) gets the correct event for the selected week
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




    // Logoff button action
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    public void onScheduleButtonClick() {
        String currentWeekStartDate = getWeekStartDate(currentWeekOffset);
        int userId = UserSession.getInstance().getUserId();
        System.out.println("User ID in SchedulerController: " + userId);
        populateScheduleTable(userId, currentWeekStartDate);
    }

    // Method to handle the "Previous Week" button click
    @FXML
    protected void onPreviousWeekButtonClick() {
        currentWeekOffset--;  // Move to the previous week
        String currentWeekStartDate = getWeekStartDate(currentWeekOffset);  // Calculate the start date for the previous week
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");  // Generate table name for the week

        // Check if the table exists, create it if not, and then populate the schedule
        checkAndCreateWeeklyTableIfNotExists(tableName, currentWeekStartDate);

        // Load the previous week's schedule
        populateScheduleTable(userId, currentWeekStartDate);
    }

    // Method to handle the "Next Week" button click
    @FXML
    protected void onNextWeekButtonClick() {
        currentWeekOffset++;  // Move to the next week
        String currentWeekStartDate = getWeekStartDate(currentWeekOffset);  // Calculate the start date for the next week
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");  // Generate table name for the week

        // Check if the table exists, create it if not, and then populate the schedule
        checkAndCreateWeeklyTableIfNotExists(tableName, currentWeekStartDate);

        // Load the next week's schedule
        populateScheduleTable(userId, currentWeekStartDate);
    }

    // Method to handle the "Current Week" button click
    @FXML
    protected void onCurrentWeekButtonClick() {
        currentWeekOffset = 0;  // Reset to the current week
        String currentWeekStartDate = getWeekStartDate(currentWeekOffset);  // Get the start date for the current week
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");  // Generate table name for the current week

        // Check if the table exists, create it if not, and then populate the schedule
        checkAndCreateWeeklyTableIfNotExists(tableName, currentWeekStartDate);

        populateScheduleTable(userId, currentWeekStartDate);
    }



    // Clear Schedule Button
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

                // Get the current week's start date
                String currentWeekStartDate = getWeekStartDate(currentWeekOffset);

                // Access the userId from the global session
                int userId = UserSession.getInstance().getUserId();

                // Call the method with both userId and currentWeekStartDate
                scheduleDAO.clearScheduleForUser(userId, currentWeekStartDate);

                showAlert("Success", "The schedule has been cleared.", Alert.AlertType.INFORMATION);
            }
        });
    }

@FXML
    public void onGenerateNewScheduleButtonClick() {
        try {
            String currentWeekStartDate = getWeekStartDate(currentWeekOffset);

            rootContainer.getChildren().clear();
            Label formTitle = new Label("Add a New Commitment For Week Start Date: " + currentWeekStartDate);

            // Dropdowns for Day, Start Time, and End Time
            ComboBox<String> dayComboBox = new ComboBox<>(FXCollections.observableArrayList(
                    "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
            ComboBox<String> timeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(timeSlots));
            ComboBox<String> endTimeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(timeSlots));

            // Text fields for event name and description
            TextField eventNameTextField = new TextField();
            eventNameTextField.setPromptText("Enter Event Name");

            TextField eventDescriptionTextField = new TextField();
            eventDescriptionTextField.setPromptText("Enter Event Description");

            // New Checkbox for Fitness Event
            // Initialize the fitness event checkbox
            fitnessEventCheckBox = new CheckBox("Is this a fitness event?");


            // Submit button logic
            Button submitButton = new Button("Submit");
            submitButton.setOnAction(event -> {
                try {
                    String dayOfWeek = dayComboBox.getValue();
                    String startTime = cleanTimeString(timeSlotComboBox.getValue());
                    String endTime = cleanTimeString(endTimeSlotComboBox.getValue());
                    String eventDescription = eventDescriptionTextField.getText();
                    String eventName = eventNameTextField.getText();
                    boolean isFitnessEvent = fitnessEventCheckBox.isSelected(); // Correctly capture the checkbox value
                    int userId = UserSession.getInstance().getUserId();

                    // Ensure all fields are filled
                    if (dayOfWeek == null || startTime.isEmpty() || endTime.isEmpty() ||
                            eventDescription.isEmpty() || eventName.isEmpty()) {
                        showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
                        return;
                    }

                    // Validate the time range
                    if (!isValidTimeRange(startTime, endTime)) {
                        showAlert("Error", "End time must be after start time.", Alert.AlertType.ERROR);
                        return;
                    }

                    // Insert the event into the database
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


            // Back button to return to the main layout
            Button backButton = new Button("Back");
            backButton.setOnAction(event -> buildInitialLayout());

            // Add all UI components to the layout
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



    // Update Event Button
    @FXML
    protected void onUpdateScheduleButtonClick() {
        try {
            // Calculate the start date for the current week offset
            String currentWeekStartDate = getWeekStartDate(currentWeekOffset);
            rootContainer.getChildren().clear();
            Label formTitle = new Label("Update an Existing Commitment");

            ComboBox<String> dayComboBox = new ComboBox<>(FXCollections.observableArrayList(
                    "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
            ComboBox<String> timeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(timeSlots));
            ComboBox<String> endTimeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(timeSlots));

            ComboBox<Schedule> eventComboBox = new ComboBox<>();

            // Add the new fitness event checkbox
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

                    // Set the fitness checkbox value based on the selected schedule
                    updateFitnessEventCheckBox.setSelected(selectedSchedule.isFitnessEvent());
                }
            });

            Button submitButton = new Button("Submit");
            submitButton.setOnAction(event -> {
                try {
                    String dayOfWeek = dayComboBox.getValue();
                    String timeSlot = timeSlotComboBox.getValue();
                    String eventDescription = eventDescriptionTextField.getText();
                    boolean isFitnessEvent = updateFitnessEventCheckBox.isSelected(); // Capture checkbox value

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
                    updateFitnessEventCheckBox,  // Add the checkbox to the layout
                    submitButton,
                    backButton
            );
        } catch (Exception e) {
            showAlert("Error", "An error occurred while updating the schedule: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


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

    // Utility method to show alerts
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void checkAndCreateWeeklyTableIfNotExists(String tableName, String weekStartDate) {
        // Check if the table already exists in the database
        if (!scheduleDAO.doesTableExist(tableName)) {
            // If the table does not exist, create it
            System.out.println("Table for the week starting on " + weekStartDate + " does not exist. Creating it now...");
            scheduleDAO.createWeeklyScheduleTable(weekStartDate, userId);  // Create the table using the week start date
        } else {
            System.out.println("Table for the week starting on " + weekStartDate + " already exists.");
        }
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

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
    private String cleanTimeString(String time) {
        return time.replaceAll("\\s+", " ").trim();  // Replace multiple spaces with one and trim any extra spaces.
    }





}