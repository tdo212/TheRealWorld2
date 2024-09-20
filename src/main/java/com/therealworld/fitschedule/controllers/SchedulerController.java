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

    // "Change Schedule" button action
    @FXML
    protected void onChangeScheduleButtonClick() {
        showAlert("Change Schedule", "Change the selected schedule.", Alert.AlertType.INFORMATION);
    }

    // "Update Schedule" button action
    @FXML
    protected void onUpdateScheduleButtonClick() {
        showAlert("Update Schedule", "Update the selected schedule.", Alert.AlertType.INFORMATION);
    }

    // Method to populate the schedule table with data from the database
    private void populateScheduleTable() {
        int userId = 1;  // Replace with the actual user ID if necessary
        List<Schedule> commitments = scheduleDAO.getScheduleForUser(userId);
        ObservableList<Schedule> tableData = FXCollections.observableArrayList(commitments);
        scheduleTable.setItems(tableData);
    }

    // "Generate New Schedule" button action handler
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

            rootContainer.getChildren().addAll(
                    formTitle,
                    new HBox(new Label("Day of the Week: "), dayComboBox),
                    new HBox(new Label("Event Name: "), eventNameTextField),
                    new HBox(new Label("Description: "), eventDescriptionTextField),
                    new HBox(new Label("Start Time: "), eventStartTimeTextField),
                    new HBox(new Label("End Time: "), eventEndTimeTextField),
                    submitButton
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

    // Method to build the initial screen layout (buttons, table, etc.)
    private void buildInitialLayout() {
        rootContainer.getChildren().clear();

        HBox schedulingHBox = new HBox(475);
        Button scheduleButton = new Button("Schedule");
        Button logoffButton = new Button("Log Off");
        schedulingHBox.setSpacing(475);
        schedulingHBox.getChildren().addAll(scheduleButton, logoffButton);

        HBox navigationHBox = new HBox(20);
        Button previousWeekButton = new Button("Previous Week");
        Button currentWeekButton = new Button("Current Week");
        Button nextWeekButton = new Button("Next Week");
        navigationHBox.setSpacing(20);
        navigationHBox.getChildren().addAll(previousWeekButton, currentWeekButton, nextWeekButton);

        Label scheduleLabel = new Label("Weekly Schedule Overview");

        HBox actionHBox = new HBox(20);
        Button changeScheduleButton = new Button("Change Schedule");
        Button updateScheduleButton = new Button("Update Schedule");
        Button generateNewScheduleButton = new Button("Generate New Schedule");
        actionHBox.getChildren().addAll(changeScheduleButton, updateScheduleButton, generateNewScheduleButton);

        rootContainer.getChildren().addAll(
                schedulingHBox,
                navigationHBox,
                scheduleLabel,
                scheduleTable,
                actionHBox
        );
    }
}
