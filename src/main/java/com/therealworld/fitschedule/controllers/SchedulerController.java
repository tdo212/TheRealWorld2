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
import javafx.scene.Node;
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

/**
 * Controller class for managing the schedule UI and handling user interactions.
 * This class is responsible for populating and managing the schedule table, as well as providing
 * user functionalities like logging off, navigating through weeks, and modifying schedules.
 */
public class SchedulerController {

    @FXML
    private VBox rootContainer;

    @FXML
    private TableView<ScheduleRow> scheduleTable;

    @FXML
    private TableColumn<ScheduleRow, String> timeSlotColumn;

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
    private final List<String> timeSlots = Arrays.asList("12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM",
            "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
            "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM");

    /**
     * Default constructor for SchedulerController.
     */
    public SchedulerController() {
    }

    /**
     * Initializes the controller. Binds table columns to their respective data fields and populates
     * the schedule table with data for the current user.
     */
    @FXML
    public void initialize() {
        int userId = UserSession.getInstance().getUserId();
        System.out.println("User ID in SchedulerController: " + userId);
        this.bindTableColumns();
        this.populateScheduleTable(userId);
    }

    /**
     * Binds each table column in the scheduleTable to the corresponding property in ScheduleRow.
     */
    private void bindTableColumns() {
        this.timeSlotColumn.setCellValueFactory((cellData) -> cellData.getValue().timeSlotProperty());
        this.mondayColumn.setCellValueFactory((cellData) -> cellData.getValue().mondayProperty());
        this.tuesdayColumn.setCellValueFactory((cellData) -> cellData.getValue().tuesdayProperty());
        this.wednesdayColumn.setCellValueFactory((cellData) -> cellData.getValue().wednesdayProperty());
        this.thursdayColumn.setCellValueFactory((cellData) -> cellData.getValue().thursdayProperty());
        this.fridayColumn.setCellValueFactory((cellData) -> cellData.getValue().fridayProperty());
        this.saturdayColumn.setCellValueFactory((cellData) -> cellData.getValue().saturdayProperty());
        this.sundayColumn.setCellValueFactory((cellData) -> cellData.getValue().sundayProperty());
    }

    /**
     * Populates the schedule table with data for the specified user.
     *
     * @param userId the ID of the user whose schedule data should be displayed
     */
    private void populateScheduleTable(int userId) {
        List<String[]> scheduleData = this.scheduleDAO.getWeeklySchedule(userId);

        if (scheduleData != null && !scheduleData.isEmpty()) {
            for (String[] row : scheduleData) {
                System.out.println("Time Slot: " + row[0] + ", Monday: " + row[1] +
                        ", Tuesday: " + row[2] + ", Wednesday: " + row[3] +
                        ", Thursday: " + row[4] + ", Friday: " + row[5] +
                        ", Saturday: " + row[6] + ", Sunday: " + row[7]);
            }
        } else {
            System.out.println("No schedule data found for user " + userId);
        }

        Map<String, String[]> scheduleMap = new HashMap<>();
        if (scheduleData != null) {
            for (String[] row : scheduleData) {
                scheduleMap.put(row[0], row);
            }
        }

        ObservableList<ScheduleRow> scheduleRows = FXCollections.observableArrayList();
        for (String timeSlot : timeSlots) {
            String[] eventRow = scheduleMap.getOrDefault(timeSlot, new String[8]);
            scheduleRows.add(new ScheduleRow(
                    timeSlot,
                    eventRow[1] == null ? "" : eventRow[1],
                    eventRow[2] == null ? "" : eventRow[2],
                    eventRow[3] == null ? "" : eventRow[3],
                    eventRow[4] == null ? "" : eventRow[4],
                    eventRow[5] == null ? "" : eventRow[5],
                    eventRow[6] == null ? "" : eventRow[6],
                    eventRow[7] == null ? "" : eventRow[7]
            ));
        }

        this.scheduleTable.setItems(scheduleRows);
        this.scheduleTable.refresh();
    }

    /**
     * Handles log off button click. Redirects the user to the login view.
     *
     * @param event the action event triggered by clicking the log off button
     * @throws IOException if there is an issue loading the login-view.fxml file
     */
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900.0, 600.0);
        stage.setScene(scene);
    }

    /**
     * Handles the Schedule button click. Refreshes the schedule table for the current user.
     */
    @FXML
    protected void onScheduleButtonClick() {
        int userId = UserSession.getInstance().getUserId();
        System.out.println("User ID in SchedulerController: " + userId);
        this.populateScheduleTable(userId);
    }

    /**
     * Handles the Previous Week button click. Displays an information alert about the previous week's schedule.
     */
    @FXML
    protected void onPreviousWeekButtonClick() {
        this.showAlert("Previous Week", "Display previous week's schedule.", Alert.AlertType.INFORMATION);
    }

    /**
     * Handles the Current Week button click. Displays an information alert about the current week's schedule.
     */
    @FXML
    protected void onCurrentWeekButtonClick() {
        this.showAlert("Current Week", "Display current week's schedule.", Alert.AlertType.INFORMATION);
    }

    /**
     * Handles the Next Week button click. Displays an information alert about the next week's schedule.
     */
    @FXML
    protected void onNextWeekButtonClick() {
        this.showAlert("Next Week", "Display next week's schedule.", Alert.AlertType.INFORMATION);
    }

    /**
     * Handles the Change Schedule button click. Prompts the user with a confirmation dialog to clear the schedule.
     * If confirmed, clears the schedule for the current user.
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
        confirmationAlert.showAndWait().ifPresent((response) -> {
            if (response == buttonYes) {
                this.scheduleTable.getItems().clear();
                this.scheduleDAO.clearScheduleForUser(1);
                this.showAlert("Success", "The schedule has been cleared.", Alert.AlertType.INFORMATION);
            }
        });
    }

    /**
     * Handles the Generate New Schedule button click. Prompts the user to add a new schedule event.
     * If the user provides the required details, the event is added to the schedule.
     */
    @FXML
    protected void onGenerateNewScheduleButtonClick() {
        try {
            this.rootContainer.getChildren().clear();
            // Set up form components for adding a new schedule event.
            Label formTitle = new Label("Add a New Commitment");
            ComboBox<String> dayComboBox = new ComboBox<>(FXCollections.observableArrayList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
            ComboBox<String> timeSlotComboBox = new ComboBox<>(FXCollections.observableArrayList(this.timeSlots));
            TextField eventNameTextField = new TextField();
            eventNameTextField.setPromptText("Enter Event Name");
            TextField eventDescriptionTextField = new TextField();
            eventDescriptionTextField.setPromptText("Enter Event Description");

            Button submitButton = new Button("Submit");
            submitButton.setOnAction((event) -> {
                try {
                    String dayOfWeek = dayComboBox.getValue();
                    String timeSlot = timeSlotComboBox.getValue();
                    String eventDescription = eventDescriptionTextField.getText();
                    int userId = UserSession.getInstance().getUserId();
                    System.out.println("User ID in SchedulerController: " + userId);
                    if (dayOfWeek == null || timeSlot == null || eventDescription.isEmpty()) {
                        this.showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
                        return;
                    }

                    this.scheduleDAO.insertWeeklyEvent(userId, timeSlot, dayOfWeek, eventDescription);
                    this.showAlert("Success", "Commitment added successfully!", Alert.AlertType.INFORMATION);
                    this.populateScheduleTable(userId);
                    this.buildInitialLayout();
                } catch (Exception var9) {
                    this.showAlert("Error", "An error occurred: " + var9.getMessage(), Alert.AlertType.ERROR);
                }
            });

            Button backButton = new Button("Back");
            backButton.setOnAction((event) -> this.buildInitialLayout());
            this.rootContainer.getChildren().addAll(formTitle, new HBox(new Label("Day of the Week: "), dayComboBox),
                    new HBox(new Label("Time Slot: "), timeSlotComboBox),
                    new HBox(new Label("Event Name: "), eventNameTextField),
                    new HBox(new Label("Description: "), eventDescriptionTextField), submitButton, backButton);
        } catch (Exception var8) {
            this.showAlert("Error", "An error occurred while generating the new schedule: " + var8.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Builds and loads the initial layout for the schedule view.
     * This method is used to reset the user interface to its original state by loading the "scheduler-view.fxml" file.
     * It clears the current contents of the `rootContainer` and replaces it with the loaded layout.
     *
     * If an IOException occurs during the loading process, an error alert is displayed to the user.
     */

    private void buildInitialLayout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("scheduler-view.fxml"));
            VBox scheduleLayout = fxmlLoader.load();
            this.rootContainer.getChildren().clear();
            this.rootContainer.getChildren().add(scheduleLayout);
        } catch (IOException var3) {
            this.showAlert("Error", "Failed to load the schedule layout: " + var3.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Displays an alert with the specified title, content, and alert type.
     *
     * @param title     the title of the alert
     * @param content   the content message of the alert
     * @param alertType the type of alert (e.g., INFORMATION, ERROR)
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void onUpdateScheduleButtonClick(ActionEvent actionEvent) {

    }
}