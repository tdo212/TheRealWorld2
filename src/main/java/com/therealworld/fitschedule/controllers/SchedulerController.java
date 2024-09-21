package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.Schedule;
import com.therealworld.fitschedule.model.SqliteDAO;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import com.therealworld.fitschedule.model.DayTracker;
import com.therealworld.fitschedule.model.Schedule;

import java.io.IOException;
import java.util.List;

public class SchedulerController {

    @FXML
    private VBox rootContainer;  // Main container that will hold all UI elements

    @FXML
    private TableView<Schedule> scheduleTable;


    @FXML
    private TableColumn<Schedule, String> mondayColumn;
    @FXML
    private TableColumn<Schedule, String> tuesdayColumn;
    @FXML
    private TableColumn<Schedule, String> wednesdayColumn;
    @FXML
    private TableColumn<Schedule, String> thursdayColumn;
    @FXML
    private TableColumn<Schedule, String> fridayColumn;
    @FXML
    private TableColumn<Schedule, String> saturdayColumn;
    @FXML
    private TableColumn<Schedule, String> sundayColumn;

    private final SqliteDAO scheduleDAO = new SqliteDAO();

    // Initialize the table when the view is loaded
    @FXML
    public void initialize() {
        bindTableColumns();
        populateScheduleTable();
        // Make columns fill the available space
        scheduleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Bind the TableColumns to the Schedule properties
    private void bindTableColumns() {
        mondayColumn.setCellValueFactory(cellData -> cellData.getValue().mondayProperty());
        tuesdayColumn.setCellValueFactory(cellData -> cellData.getValue().tuesdayProperty());
        wednesdayColumn.setCellValueFactory(cellData -> cellData.getValue().wednesdayProperty());
        thursdayColumn.setCellValueFactory(cellData -> cellData.getValue().thursdayProperty());
        fridayColumn.setCellValueFactory(cellData -> cellData.getValue().fridayProperty());
        saturdayColumn.setCellValueFactory(cellData -> cellData.getValue().saturdayProperty());
        sundayColumn.setCellValueFactory(cellData -> cellData.getValue().sundayProperty());
    }

    // Logoff button action
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    // "Schedule" button action to create or insert a mock schedule
    @FXML
    protected void onScheduleButtonClick() {
        // Insert mock data into the database for user with ID 1 (replace with actual ID if necessary)
        scheduleDAO.insertSchedule(1, "Tuesday", "Workout", "Gym session", "10:00 AM", "11:00 AM");
        populateScheduleTable();
    }

    // "Previous Week" button action
    @FXML
    protected void onPreviousWeekButtonClick() {
        showAlert("Previous Week", "Display previous week's schedule.", Alert.AlertType.INFORMATION);
    }

    // "Current Week" button action
    @FXML
    protected void onCurrentWeekButtonClick() {
        showAlert("Current Week", "Display current week's schedule.", Alert.AlertType.INFORMATION);
    }

    // "Next Week" button action
    @FXML
    protected void onNextWeekButtonClick() {
        showAlert("Next Week", "Display next week's schedule.", Alert.AlertType.INFORMATION);
    }

    @FXML
    protected void onChangeScheduleButtonClick() {
        // Create a confirmation alert dialog
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Clear Schedule Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to clear the schedule?");
        confirmationAlert.setContentText("This action cannot be undone.");

        // Add buttons for the user to confirm or cancel
        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirmationAlert.getButtonTypes().setAll(buttonYes, buttonNo);

        // Show the alert and wait for the user's response
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                // User confirmed, clear the table
                scheduleTable.getItems().clear();

                scheduleDAO.clearScheduleForUser(1);  // Assuming 1 is the user ID, replace as needed

                showAlert("Success", "The schedule has been cleared.", Alert.AlertType.INFORMATION);
            } else if (response == buttonNo) {
                // User chose "No", do nothing and close the dialog
                confirmationAlert.close();
            }
        });
    }

    @FXML
    protected void onUpdateScheduleButtonClick() {
        try {
            // Clear the root container
            rootContainer.getChildren().clear();

            // Title for the update form
            Label formTitle = new Label("Update an Existing Commitment");

            // Create dropdown for day selection
            ComboBox<String> dayComboBox = new ComboBox<>();
            dayComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

            // Dropdown for selecting an event once day is selected
            ComboBox<Schedule> eventComboBox = new ComboBox<>();

            // Populate the event dropdown based on the selected day
            dayComboBox.setOnAction(event -> {
                String selectedDay = dayComboBox.getValue();
                if (selectedDay != null) {
                    List<Schedule> commitments = scheduleDAO.getCommitmentsForDay(1, selectedDay);  // User ID is 1, change as needed
                    eventComboBox.setItems(FXCollections.observableArrayList(commitments));
                }
            });

            // Text fields for event details (same as in generate new schedule)
            TextField eventNameTextField = new TextField();
            TextField eventDescriptionTextField = new TextField();
            TextField eventStartTimeTextField = new TextField();
            TextField eventEndTimeTextField = new TextField();

            eventComboBox.setOnAction(event -> {
                Schedule selectedSchedule = eventComboBox.getValue();
                if (selectedSchedule != null) {
                    // Populate the form fields with the selected event's existing data
                    eventNameTextField.setText(selectedSchedule.getEventName());
                    eventDescriptionTextField.setText(selectedSchedule.getEventDescription());
                    eventStartTimeTextField.setText(selectedSchedule.getEventStartTime());
                    eventEndTimeTextField.setText(selectedSchedule.getEventEndTime());
                }
            });

            // Add the checkbox for "Is this a workout?"
            CheckBox isWorkoutCheckBox = new CheckBox("Is this a workout?");


            // Button to submit the updated data
            Button submitButton = new Button("Submit");

            submitButton.setOnAction(event -> {
                try {
                    Schedule selectedSchedule = eventComboBox.getValue();
                    if (selectedSchedule == null) {
                        showAlert("Error", "No event selected for update.", Alert.AlertType.ERROR);
                        return;
                    }

                    // Get updated fields
                    String dayOfWeek = dayComboBox.getValue();
                    String eventName = eventNameTextField.getText();
                    String eventDescription = eventDescriptionTextField.getText();
                    String eventStartTime = eventStartTimeTextField.getText();
                    String eventEndTime = eventEndTimeTextField.getText();

                    // Validate inputs
                    if (dayOfWeek == null || eventName.isEmpty() || eventDescription.isEmpty() || eventStartTime.isEmpty() || eventEndTime.isEmpty()) {
                        showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
                        return;
                    }

                    // Delete the old schedule and insert the new one (you can modify this to update the existing row if your DB supports it)
                    scheduleDAO.deleteSchedule(selectedSchedule.getId());  // Assuming there's an ID field to identify the record
                    scheduleDAO.insertSchedule(1, dayOfWeek, eventName, eventDescription, eventStartTime, eventEndTime);

                    showAlert("Success", "Commitment updated successfully!", Alert.AlertType.INFORMATION);

                    buildInitialLayout();  // Recreate initial layout
                    populateScheduleTable();  // Refresh the table with new data
                } catch (Exception e) {
                    showAlert("Error", "An error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            // Create the "Back" button to go back to the main schedule view
            Button backButton = new Button("Back");
            backButton.setOnAction(event -> buildInitialLayout());

            // Add all elements to the root container
            rootContainer.getChildren().addAll(
                    formTitle,
                    new HBox(new Label("Day of the Week: "), dayComboBox),
                    new HBox(new Label("Select Event: "), eventComboBox),
                    new HBox(new Label("Event Name: "), eventNameTextField),
                    new HBox(new Label("Description: "), eventDescriptionTextField),
                    new HBox(new Label("Start Time: "), eventStartTimeTextField),
                    new HBox(new Label("End Time: "), eventEndTimeTextField),
                    isWorkoutCheckBox,  // Add the checkbox to the form
                    submitButton,
                    backButton  // Add the "Back" button to the layout
            );
        } catch (Exception e) {
            showAlert("Error", "An error occurred while updating the schedule: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private final DayTracker dayTracker = new DayTracker();  // Instance of the DayTracker class

    // Method to populate the schedule table with data from the database
    private void populateScheduleTable() {
        int userId = 1;  // Replace with the actual user ID if necessary
        List<Schedule> commitments = scheduleDAO.getScheduleForUser(userId);
        ObservableList<Schedule> tableData = FXCollections.observableArrayList(commitments);
        scheduleTable.setItems(tableData);

        // Highlight events happening today and at the current time
        highlightCurrentEvents(tableData);
    }

    // Method to highlight events happening today and at the current time
    private void highlightCurrentEvents(ObservableList<Schedule> tableData) {
        List<Schedule> eventsHappeningNow = dayTracker.getEventsHappeningNow(tableData);

        // Add custom CSS style to highlight rows
        scheduleTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Schedule item, boolean empty) {
                super.updateItem(item, empty);

                // Remove previous styles
                setStyle("");

                // Check if the current row item is one of the events happening now
                if (item != null && eventsHappeningNow.contains(item)) {
                    setStyle("-fx-background-color: yellow;");  // Highlight with yellow color
                }
            }
        });
    }




    @FXML
    protected void onGenerateNewScheduleButtonClick() {
        try {
            // Clear the entire root container
            rootContainer.getChildren().clear();

            // Create new input fields dynamically for adding a new schedule
            Label formTitle = new Label("Add a New Commitment");

            ComboBox<String> dayComboBox = new ComboBox<>();
            dayComboBox.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

            TextField eventNameTextField = new TextField();
            eventNameTextField.setPromptText("Enter Event Name");

            TextField eventDescriptionTextField = new TextField();
            eventDescriptionTextField.setPromptText("Enter Event Description");

            TextField eventStartTimeTextField = new TextField();
            eventStartTimeTextField.setPromptText("Start Time (e.g. 10:00 AM)");

            TextField eventEndTimeTextField = new TextField();
            eventEndTimeTextField.setPromptText("End Time (e.g. 11:00 AM)");

            // Add the checkbox for "Is this a workout?"
            CheckBox isWorkoutCheckBox = new CheckBox("Is this a workout?");

            Button submitButton = new Button("Submit");

            submitButton.setOnAction(event -> {
                try {
                    String dayOfWeek = dayComboBox.getValue();
                    String eventName = eventNameTextField.getText();
                    String eventDescription = eventDescriptionTextField.getText();
                    String eventStartTime = eventStartTimeTextField.getText();
                    String eventEndTime = eventEndTimeTextField.getText();

                    if (dayOfWeek == null || eventName.isEmpty() || eventDescription.isEmpty() || eventStartTime.isEmpty() || eventEndTime.isEmpty()) {
                        showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
                        return;
                    }

                    scheduleDAO.insertSchedule(1, dayOfWeek, eventName, eventDescription, eventStartTime, eventEndTime);

                    showAlert("Success", "Commitment added successfully!", Alert.AlertType.INFORMATION);

                    buildInitialLayout();  // Recreate initial layout
                    populateScheduleTable();  // Refresh the table with new data
                } catch (Exception e) {
                    showAlert("Error", "An error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            // Create the "Back" button to go back to the main schedule view
            Button backButton = new Button("Back");
            backButton.setOnAction(event -> buildInitialLayout());

            // Add all elements to the root container
            rootContainer.getChildren().addAll(
                    formTitle,
                    new HBox(new Label("Day of the Week: "), dayComboBox),
                    new HBox(new Label("Event Name: "), eventNameTextField),
                    new HBox(new Label("Description: "), eventDescriptionTextField),
                    new HBox(new Label("Start Time: "), eventStartTimeTextField),
                    new HBox(new Label("End Time: "), eventEndTimeTextField),
                    isWorkoutCheckBox,  // Add the checkbox to the form
                    submitButton,
                    backButton  // Add the "Back" button to the layout
            );
        } catch (Exception e) {
            showAlert("Error", "An error occurred while generating the new schedule: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    // Utility method to show alert messages
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void buildInitialLayout() {
        try {
            // Load the FXML file for the schedule view
            FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("scheduler-view.fxml"));
            VBox scheduleLayout = fxmlLoader.load();

            // Clear the root container and add the loaded layout from FXML
            rootContainer.getChildren().clear();
            rootContainer.getChildren().add(scheduleLayout);
        } catch (IOException e) {
            showAlert("Error", "Failed to load the schedule layout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
