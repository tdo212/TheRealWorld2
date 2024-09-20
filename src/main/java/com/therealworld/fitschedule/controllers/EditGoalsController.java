package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SqliteDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditGoalsController {

    public ComboBox<String> goalTypeComboBox; // For selecting between 'Cardio', 'Gym', etc.
    public ComboBox<String> durationTypeComboBox; // For selecting between 'Hours per week', 'Days per week'
    public ComboBox<Integer> goalDurationComboBox; // For selecting 4, 5, 6, up to 12 weeks
    public TextField targetSessionsField; // For entering target number of sessions or hours
    @FXML
    public TextField otherGoalTextField; // For entering custom goal type when 'Other' is selected

    private SqliteDAO DbDAO;  // Create an instance of SqliteDAO (non-static)

    public void initialize() {
        // Initialize ComboBoxes with options

        goalTypeComboBox.getItems().clear();
        durationTypeComboBox.getItems().clear();
        goalDurationComboBox.getItems().clear();

        goalTypeComboBox.getItems().addAll("Cardio", "Gym", "Other");
        durationTypeComboBox.getItems().addAll("Hours per week", "Days per week");
        goalDurationComboBox.getItems().addAll(4, 5, 6, 7, 8, 9, 10, 11, 12);

        // Create the DAO instance
        DbDAO = new SqliteDAO();  // You can now call non-static methods like addGoal

        // Add listener to show/hide 'Other' goal text field
        goalTypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                otherGoalTextField.setVisible("Other".equals(newValue));
            }
        });

        otherGoalTextField.setVisible(false); // Initially hidden
    }

    // Handle saving the goal and then closing the window
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
            int targetValue = Integer.parseInt(targetSessions); // Ensure target is a number

            // Assuming userId is available, replace 1 with actual user ID from session
            int userId = 1;  // Replace with actual user ID

            // Now use the instance of DbDAO to call the non-static method
            DbDAO.addGoal(userId, goalType, goalDuration, durationType, "Goal Description");

            showAlert("Goal saved successfully!", Alert.AlertType.INFORMATION);
            closeCurrentWindow(actionEvent);

        } catch (NumberFormatException e) {
            showAlert("Target value must be a number!", Alert.AlertType.ERROR);
        }
    }
    // Handle cancelling, simply close the current edit-goals-view window
    public void onCancelClick(ActionEvent actionEvent) {
        closeCurrentWindow(actionEvent);
    }

    // Close the window
    private void closeCurrentWindow(ActionEvent actionEvent) {
        Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        currentStage.close();  // Close the current window
    }

    // Helper to show alerts
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
