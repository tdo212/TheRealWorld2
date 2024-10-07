package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SqliteDAO;
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
/**
 * The EditGoalsController handles the user interface for editing or adding goals.
 * It allows users to select goal types, duration, and target sessions, and interact with the database
 * to store these goals. It also allows users to cancel or save their inputs.
 */
public class EditGoalsController {
    /**
     * ComboBox for selecting the type of goal (e.g., 'Cardio', 'Gym', or 'Other').
     */
    public ComboBox<String> goalTypeComboBox; // For selecting between 'Cardio', 'Gym', etc.
    /**
     * ComboBox for selecting the duration type (e.g., 'Hours per week', 'Days per week').
     */
    public ComboBox<String> durationTypeComboBox; // For selecting between 'Hours per week', 'Days per week'
    /**
     * ComboBox for selecting the duration of the goal in weeks (ranging from 4 to 12 weeks).
     */
    public ComboBox<Integer> goalDurationComboBox; // For selecting 4, 5, 6, up to 12 weeks
    /**
     * TextField for entering the target number of sessions or hours for the goal.
     */
    public TextField targetSessionsField; // For entering target number of sessions or hours
    /**
     * TextField for entering a custom goal type when 'Other' is selected.
     */
    @FXML
    public TextField otherGoalTextField; // For entering custom goal type when 'Other' is selected
    /**
     * Data access object for performing operations on the database.
     */
    private SqliteDAO goalsDAO;
    /**
     * Reference to the GoalsController for refreshing the goal list after editing or adding a goal.
     */
    private GoalsController goalsController;  // Reference to GoalsController

    /**
     * Injects the GoalsController to establish communication between the two controllers.
     * This allows the EditGoalsController to refresh the goal list in GoalsController after an update.
     *
     * @param goalsController the instance of GoalsController to be set.
     */
    public void setGoalsController(GoalsController goalsController) {
        this.goalsController = goalsController;
    }
    /**
     * Initializes the controller by populating ComboBoxes with predefined values and setting up
     * listeners for dynamic UI changes (such as showing the 'Other' goal text field when selected).
     */
    public void initialize() {
        // Initialize ComboBoxes with options

        // Clear any existing items first to prevent duplicates
        goalTypeComboBox.getItems().clear();
        durationTypeComboBox.getItems().clear();
        goalDurationComboBox.getItems().clear();

        // Add items to the goal type ComboBox
        goalTypeComboBox.getItems().addAll("Cardio", "Gym", "Other");

        // Add items to the duration type ComboBox
        durationTypeComboBox.getItems().addAll("Hours per week", "Days per week");

        // Add items to the goal duration ComboBox (for 4-12 weeks)
        goalDurationComboBox.getItems().addAll(4, 5, 6, 7, 8, 9, 10, 11, 12);

        // Create DAO instance
        goalsDAO = new SqliteDAO();

        // Add listener to the goalTypeComboBox to show/hide the otherGoalTextField
        goalTypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if ("Other".equals(newValue)) {
                    otherGoalTextField.setVisible(true);
                } else {
                    otherGoalTextField.setVisible(false);
                }
            }
        });

        // Initially hide the otherGoalTextField
        otherGoalTextField.setVisible(false);
    }
    /**
     * Handles saving the goal input by the user. Validates the inputs, and if valid,
     * saves the goal to the database and notifies the GoalsController to refresh the list.
     *
     * @param actionEvent the event triggered by clicking the 'Save' button.
     */
    // Handle saving the goal and then closing the window
    public void onSaveClick(ActionEvent actionEvent) {
        String goalType = goalTypeComboBox.getValue();
        String durationType = durationTypeComboBox.getValue();
        Integer goalDuration = goalDurationComboBox.getValue(); // This is an Integer
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

            // Check if target value is zero
            if (targetValue == 0) {
                showAlert("Target value cannot be zero!", Alert.AlertType.ERROR);
                return;
            }

            // Assuming userId is available, you should pass it from the logged-in user's session
            int userId = 1; // Example user ID, replace with actual user ID

            // Since we're adding a new goal, it's not completed, so set goalCompleted to 0 (false)
            int goalCompleted = 0;

            // Save the goal to the database, now including the goal_completed field
            goalsDAO.addGoal(userId, goalType, goalDuration, durationType, "Goal Description", goalCompleted);

            showAlert("Goal saved successfully!", Alert.AlertType.INFORMATION);

            // Notify the GoalsController to refresh the list of goals
            if (goalsController != null) {
                goalsController.refreshGoalsList();  // Refresh the goal list
            }

            // Close the current edit-goals-view window after saving
            closeCurrentWindow(actionEvent);

        } catch (NumberFormatException e) {
            showAlert("Target value must be a number!", Alert.AlertType.ERROR);
        }
    }
    /**
     * Handles the cancellation of the goal input by closing the window without saving any changes.
     *
     * @param actionEvent the event triggered by clicking the 'Cancel' button.
     */
    // Handle cancelling, simply close the current edit-goals-view window
    public void onCancelClick(ActionEvent actionEvent) {
        closeCurrentWindow(actionEvent);
    }
    /**
     * Closes the current window that contains the edit-goals-view.
     *
     * @param actionEvent the event triggered by clicking the save or cancel buttons.
     */
    // Helper method to close the current window
    private void closeCurrentWindow(ActionEvent actionEvent) {
        // Get the current stage (the window where the cancel/save button was clicked)
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();  // Close the current window
    }
    /**
     * Displays an alert dialog with a specified message and alert type.
     *
     * @param message the message to be displayed in the alert dialog.
     * @param type    the type of the alert (e.g., ERROR, INFORMATION).
     */
    // Helper method to show alerts
    void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
