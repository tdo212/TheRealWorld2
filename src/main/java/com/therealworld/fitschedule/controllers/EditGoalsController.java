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

public class EditGoalsController {

    public ComboBox<String> goalTypeComboBox; // For selecting between 'Cardio', 'Gym', etc.
    public ComboBox<String> durationTypeComboBox; // For selecting between 'Hours per week', 'Days per week'
    public ComboBox<Integer> goalDurationComboBox; // For selecting 4, 5, 6, up to 12 weeks
    public TextField targetSessionsField; // For entering target number of sessions or hours
    @FXML
    public TextField otherGoalTextField; // For entering custom goal type when 'Other' is selected

    private SqliteDAO goalsDAO;
    private GoalsController goalsController;  // Reference to GoalsController

    // Inject the GoalsController
    public void setGoalsController(GoalsController goalsController) {
        this.goalsController = goalsController;
    }

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
            //int userId = 12; // Example user ID, replace with actual user ID
            int userId = UserSession.getInstance().getUserId();

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

    // Handle cancelling, simply close the current edit-goals-view window
    public void onCancelClick(ActionEvent actionEvent) {
        closeCurrentWindow(actionEvent);
    }

    // Helper method to close the current window
    private void closeCurrentWindow(ActionEvent actionEvent) {
        // Get the current stage (the window where the cancel/save button was clicked)
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();  // Close the current window
    }

    // Helper method to show alerts
    void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
