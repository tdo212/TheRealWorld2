package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SqliteDAO;
import com.therealworld.fitschedule.model.UserSession;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static com.therealworld.fitschedule.model.User.username;

/**
 * Controller for editing goals, allowing users to input details for new goals or modify existing ones.
 */
public class EditGoalsController {

    public ComboBox<String> goalTypeComboBox; // ComboBox for selecting goal type (e.g., 'Cardio', 'Gym')
    public ComboBox<String> durationTypeComboBox; // ComboBox for selecting duration type (e.g., 'Hours per week')
    public ComboBox<Integer> goalDurationComboBox; // ComboBox for selecting goal duration (e.g., 4-12 weeks)
    public TextField targetSessionsField; // TextField for entering target number of sessions or hours
    @FXML
    public TextField otherGoalTextField; // TextField for custom goal type entry when 'Other' is selected

    private SqliteDAO goalsDAO;
    private GoalsController goalsController;  // Reference to the main GoalsController

    /**
     * Injects the GoalsController instance for interaction with the main goals view.
     *
     * @param goalsController the main GoalsController instance to communicate with
     */
    public void setGoalsController(GoalsController goalsController) {
        this.goalsController = goalsController;
    }

    /**
     * Initializes the EditGoalsController by populating ComboBoxes, setting up listeners, and configuring UI elements.
     */
    public void initialize() {
        // Clear any existing items and populate the ComboBoxes
        goalTypeComboBox.getItems().clear();
        durationTypeComboBox.getItems().clear();
        goalDurationComboBox.getItems().clear();

        // Populate goal type, duration type, and goal duration options
        goalTypeComboBox.getItems().addAll("Cardio", "Gym", "Other");
        durationTypeComboBox.getItems().addAll("Hours per week", "Days per week");
        goalDurationComboBox.getItems().addAll(4, 5, 6, 7, 8, 9, 10, 11, 12);

        // Create DAO instance
        goalsDAO = new SqliteDAO();

        // Add listener to show/hide custom goal text field if "Other" is selected
        goalTypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                otherGoalTextField.setVisible("Other".equals(newValue));
            }
        });

        // Initially hide the custom goal field
        otherGoalTextField.setVisible(false);
    }

    /**
     * Handles the save action to store a new or modified goal in the database and update the main goals view.
     *
     * @param actionEvent the event triggered when the save button is clicked
     */
    public void onSaveClick(ActionEvent actionEvent) {
        String goalType = goalTypeComboBox.getValue();
        String durationType = durationTypeComboBox.getValue();
        Integer goalDuration = goalDurationComboBox.getValue();
        String targetSessions = targetSessionsField.getText();

        if (goalType == null || durationType == null || goalDuration == null || targetSessions.isEmpty()) {
            showAlert("Please fill out all the required fields!", Alert.AlertType.ERROR);
            return;
        }

        if ("Other".equals(goalType)) {
            goalType = otherGoalTextField.getText();
        }

        try {
            int targetValue = Integer.parseInt(targetSessions); // Ensure the target value is a number

            if (targetValue == 0) {
                showAlert("Target value cannot be zero!", Alert.AlertType.ERROR);
                return;
            }

            int userId = UserSession.getInstance().getUserId(); // Get user ID from session

            // Set goal as incomplete (0) when initially saved
            int goalCompleted = 0;

            // Add the new goal to the database
            goalsDAO.addGoal(userId, goalType, goalDuration, durationType, "Goal Description", goalCompleted);

            showAlert("Goal saved successfully!", Alert.AlertType.INFORMATION);

            // Refresh the goals list in the main GoalsController if available
            if (goalsController != null) {
                goalsController.refreshGoalsList();
            }

            // Close the edit window
            closeCurrentWindow(actionEvent);

        } catch (NumberFormatException e) {
            showAlert("Target value must be a number!", Alert.AlertType.ERROR);
        }
    }

    /**
     * Cancels the current goal editing session, closing the window without saving changes.
     *
     * @param actionEvent the event triggered when the cancel button is clicked
     */
    public void onCancelClick(ActionEvent actionEvent) {
        closeCurrentWindow(actionEvent);
    }

    /**
     * Closes the current edit goals window.
     *
     * @param actionEvent the event triggered by the button click to close the window
     */
    private void closeCurrentWindow(ActionEvent actionEvent) {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();
    }

    /**
     * Displays an alert dialog with a given message and type.
     *
     * @param message the message to display in the alert
     * @param type    the type of alert (e.g., ERROR, INFORMATION)
     */
    void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

