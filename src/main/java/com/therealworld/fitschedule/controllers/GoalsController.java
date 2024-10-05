package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.Goal;
import com.therealworld.fitschedule.model.SqliteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GoalsController {

    @FXML
    private ListView<Goal> contactsListView;
    @FXML
    private Label goalCountLabel; // Label to display goal count
    @FXML
    private Label goalCompletedLabel; // Label to display completed goals count
    @FXML
    private PieChart pieChart;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;  // Label to display percentage

    private SqliteDAO databaseHelper = new SqliteDAO();
    private int goalsCompleted = 0;

    public void initialize() {
        refreshGoalsList(); // Initialize the list of goals
        displayGoalCount();
        displayPieChart();
        updateProgressBar();
    }

    // Method to refresh the list of goals and update the UI
    public void refreshGoalsList() {
        ObservableList<Goal> data = databaseHelper.getAllGoals(); // Ensure SqliteDAO returns ObservableList<Goal>

        System.out.println("Number of items to display: " + data.size());

        // Set the items in the ListView
        contactsListView.setItems(data);
        // Customize how each Goal is displayed in the ListView
        contactsListView.setCellFactory(param -> new ListCell<Goal>() {
            @Override
            protected void updateItem(Goal goal, boolean empty) {
                super.updateItem(goal, empty);
                if (empty || goal == null) {
                    setText(null);
                } else {
                    // Format the display string to show all goal details
                    String goalDetails = String.format(
                            "ID: %d, Type: %s, Duration: %d weeks, Period: %s, Description: %s, Completed: %s",
                            goal.getGoalId(),
                            goal.getGoalType(),
                            goal.getGoalDuration(),
                            goal.getGoalPeriod(),
                            goal.getGoalDescription(),
                            goal.isGoalCompleted() ? "Yes" : "No" // Display if the goal is completed
                    );
                    setText(goalDetails);
                }
            }
        });

        displayGoalCount();
        displayPieChart();
        updateProgressBar();
        updateGoalsCompleted();

    }

    public void displayGoalCount() {
        int goalCount = databaseHelper.countGoals();
        goalCountLabel.setText("Goals Remaining: " + goalCount);
    }

    public void updateGoalsCompleted() {
        int goalsCompleted = 0;
        goalCompletedLabel.setText("Goals Completed: " + completedGoals);
    }

    public void displayPieChart() {
        int goalCount = databaseHelper.countGoals();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Completed", completedGoals),
                new PieChart.Data("Incomplete", goalCount)
        );
        pieChart.setData(pieChartData); // Update pie chart with new data
    }

    public void updateProgressBar() {
        int goalCount = databaseHelper.countGoals(); // Fetch the total number of goals
       int completedGoals = databaseHelper.getCompletedGoalsCount(); // Fetch the total number of completed goals

        // Avoid division by zero
        if (goalCount == 0) {
            progressBar.setProgress(0);  // Set progress to 0 if no goals exist
            progressLabel.setText("0%");  // Display 0% progress
            return;
        }

        // Calculate progress as a double value
        double progressgoals = (double) completedGoals / goalCount;

        // Set the progress bar based on the calculated value
        progressBar.setProgress(progressgoals);

        // Convert the progress value to a percentage and update the label
        int progressPercentage = (int) (progressgoals * 100);
        progressLabel.setText(progressPercentage + "%");
    }


    @FXML
    public void onEditGoalsClick(ActionEvent event) {
        try {
            // Load the Edit Goals FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/therealworld/fitschedule/edit-goals-view.fxml"));
            Parent root = fxmlLoader.load();

            // Pass the GoalsController reference to the EditGoalsController
            EditGoalsController editGoalsController = fxmlLoader.getController();
            editGoalsController.setGoalsController(this); // Pass the current instance of GoalsController

            // Create a new stage (window) for the Edit Goals UI
            Stage stage = new Stage();
            stage.setTitle("Edit Goals");
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait until the edit goals window is closed

            // Refresh the goals list after the edit window is closed
            refreshGoalsList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int completedGoals = 0; // New stat for tracking goals completed

    @FXML
    public void onCompleteGoalsClick(ActionEvent event) {
        Goal selectedGoal = contactsListView.getSelectionModel().getSelectedItem();

        if (selectedGoal != null && !selectedGoal.isGoalCompleted()) {
            contactsListView.getItems().remove(selectedGoal); // Remove the selected goal from the ListView
            databaseHelper.updateGoalAsCompleted(selectedGoal.getGoalId());
            databaseHelper.deleteGoalFromDatabase(selectedGoal.getGoalId()); // Delete from database
            completedGoals++;
            updateGoalsCompleted(); // Update goals completed count
            updateProgressBar(); // Update progress bar
            displayPieChart(); // Update the pie chart
            displayGoalCount();

        }
    }

    @FXML
    public void onDeleteGoalsClick(ActionEvent event) {
        Goal selectedGoal = contactsListView.getSelectionModel().getSelectedItem();

        if (selectedGoal != null) {
            databaseHelper.deleteGoalFromDatabase(selectedGoal.getGoalId()); // Delete from database

            // Refresh the goal list after deletion
            refreshGoalsList();
        }
    }
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }
}
