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
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SchedulerController {

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
        // Access the userId from the global session
        int userId = UserSession.getInstance().getUserId();
        System.out.println("User ID in SchedulerController: " + userId);
        // Bind the TableColumns to ScheduleRow properties
        bindTableColumns();
        populateScheduleTable(userId);
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

    // Method to populate the schedule table for a specific user
    private void populateScheduleTable(int userId) {
        List<String> timeSlots = Arrays.asList(
                "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM",
                "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
                "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
        );

        // Retrieve the weekly schedule data for the specified user
        List<String[]> scheduleData = scheduleDAO.getWeeklySchedule(userId);

        // Log the retrieved schedule data for debugging
        if (scheduleData == null || scheduleData.isEmpty()) {
            System.out.println("No schedule data found for user " + userId);
        } else {
            for (String[] row : scheduleData) {
                System.out.println("Time Slot: " + row[0] + ", Monday: " + row[1] + ", Tuesday: " + row[2] +
                        ", Wednesday: " + row[3] + ", Thursday: " + row[4] + ", Friday: " + row[5] +
                        ", Saturday: " + row[6] + ", Sunday: " + row[7]);
            }
        }

        // Create a map to store the schedule by time slot for easy lookup
        Map<String, String[]> scheduleMap = new HashMap<>();
        if (scheduleData != null) {
            for (String[] row : scheduleData) {
                scheduleMap.put(row[0], row);  // Time slot as the key
            }
        }

        // Initialize the observable list for the table rows
        ObservableList<ScheduleRow> scheduleRows = FXCollections.observableArrayList();

        // Populate the table with predefined time slots and merge with any existing events
        for (String timeSlot : timeSlots) {
            // Retrieve the schedule row for this time slot from the map
            String[] eventRow = scheduleMap.getOrDefault(timeSlot, new String[8]);

            // Add the row to the table, ensuring that each day (column) gets the correct event
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

    // "Schedule" button action to insert mock schedule data
    @FXML
    protected void onScheduleButtonClick() {
        // Access the userId from the global session
        int userId = UserSession.getInstance().getUserId();
        System.out.println("User ID in SchedulerController: " + userId);;
        populateScheduleTable(userId);
    }

    // Week navigation button actions
    @FXML
    protected void onPreviousWeekButtonClick() {
        showAlert("Previous Week", "Display previous week's schedule.", Alert.AlertType.INFORMATION);
    }

    @FXML
    protected void onCurrentWeekButtonClick() {
        showAlert("Current Week", "Display current week's schedule.", Alert.AlertType.INFORMATION);
    }

    @FXML
    protected void onNextWeekButtonClick() {
        showAlert("Next Week", "Display next week's schedule.", Alert.AlertType.INFORMATION);
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
                scheduleDAO.clearScheduleForUser(1);  // Replace with actual user ID
                showAlert("Success", "The schedule has been cleared.", Alert.AlertType.INFORMATION);
            }
        });
    }

    // Add New Event Button
    @FXML
    protected void onGenerateNewScheduleButtonClick() {
        try {
            rootContainer.getChildren().clear();
            Label formTitle = new Label("Add a New Commitment");

            ComboBox<String> dayComboBox = new ComboBox<>(FXCollections.observableArrayList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
            ComboBox<String> timeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(timeSlots));

            TextField eventNameTextField = new TextField();
            eventNameTextField.setPromptText("Enter Event Name");

            TextField eventDescriptionTextField = new TextField();
            eventDescriptionTextField.setPromptText("Enter Event Description");

            Button submitButton = new Button("Submit");
            submitButton.setOnAction(event -> {
                try {
                    String dayOfWeek = dayComboBox.getValue();
                    String timeSlot = timeSlotComboBox.getValue();
                    String eventDescription = eventDescriptionTextField.getText();
                    // Access the userId from the global session
                    int userId = UserSession.getInstance().getUserId();
                    System.out.println("User ID in SchedulerController: " + userId);

                    if (dayOfWeek == null || timeSlot == null || eventDescription.isEmpty()) {
                        showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
                        return;
                    }

                    scheduleDAO.insertWeeklyEvent(userId, timeSlot, dayOfWeek, eventDescription);
                    showAlert("Success", "Commitment added successfully!", Alert.AlertType.INFORMATION);

                    // Refresh the table to display the new event
                    populateScheduleTable(userId);

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
                    new HBox(new Label("Time Slot: "), timeSlotComboBox),
                    new HBox(new Label("Event Name: "), eventNameTextField),
                    new HBox(new Label("Description: "), eventDescriptionTextField),
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
            rootContainer.getChildren().clear();
            Label formTitle = new Label("Update an Existing Commitment");

            ComboBox<String> dayComboBox = new ComboBox<>(FXCollections.observableArrayList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
            ComboBox<String> timeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(timeSlots));

            ComboBox<Schedule> eventComboBox = new ComboBox<>();
            dayComboBox.setOnAction(event -> {
                String selectedDay = dayComboBox.getValue();
                if (selectedDay != null) {
                    List<Schedule> commitments = scheduleDAO.getCommitmentsForDay(1, selectedDay);
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
                }
            });

            Button submitButton = new Button("Submit");
            submitButton.setOnAction(event -> {
                try {
                    String dayOfWeek = dayComboBox.getValue();
                    String timeSlot = timeSlotComboBox.getValue();
                    String eventDescription = eventDescriptionTextField.getText();
                    // Access the userId from the global session
                    int userId = UserSession.getInstance().getUserId();
                    System.out.println("User ID in SchedulerController: " + userId);

                    if (dayOfWeek == null || timeSlot == null || eventDescription.isEmpty()) {
                        showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
                        return;
                    }

                    scheduleDAO.insertWeeklyEvent(userId, timeSlot, dayOfWeek, eventDescription);
                    showAlert("Success", "Commitment added successfully!", Alert.AlertType.INFORMATION);

                    // Refresh the table to display the new event
                    populateScheduleTable(userId);

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
                    new HBox(new Label("Time Slot: "), timeSlotComboBox),
                    new HBox(new Label("Event Name: "), eventNameTextField),
                    new HBox(new Label("Description: "), eventDescriptionTextField),
                    new HBox(new Label("Select Event: "), eventComboBox),
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
}