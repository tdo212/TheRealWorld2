package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SqliteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GoalsController {

    @FXML
    private ListView<String> contactsListView;
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
        ObservableList<String> data = SqliteDAO.getAllGoals(); // Fetch goals from the database
        System.out.println("Number of items to display: " + data.size());
        contactsListView.setItems(data); // Set goals in the ListView
        displayGoalCount();
        displayPieChart();
        updateProgressBar();
    }

    public void displayGoalCount() {
        int goalCount = databaseHelper.countGoals();
        goalCountLabel.setText("Goals Remaining: " + goalCount);
    }

    public void updateGoalsCompleted() {
        goalsCompleted += 1;
        goalCompletedLabel.setText("Goals Completed: " + goalsCompleted);
    }

    public void displayPieChart() {
        int goalCount = databaseHelper.countGoals();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Completed", goalsCompleted),
                new PieChart.Data("Incomplete", goalCount)
        );
        pieChart.setData(pieChartData); // Update pie chart with new data
    }

    public void updateProgressBar() {
        int goalCount = databaseHelper.countGoals();
        double progressgoals = (double) goalsCompleted / goalCount;
        progressBar.setProgress(progressgoals);
        int progresslabel = (int) (progressgoals * 100);
        progressLabel.setText(progresslabel + "%");
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

    @FXML
    public void onCompleteGoalsClick(ActionEvent event) {
        String selectedGoal = contactsListView.getSelectionModel().getSelectedItem();

        if (selectedGoal != null) {
            contactsListView.getItems().remove(selectedGoal); // Remove the selected goal from the ListView
            updateGoalsCompleted(); // Update goals completed count
            updateProgressBar(); // Update progress bar
            displayPieChart(); // Update the pie chart
        }
    }

    @FXML
    public void onDeleteGoalsClick(ActionEvent event) {
        String selectedGoal = contactsListView.getSelectionModel().getSelectedItem();

        if (selectedGoal != null) {
            contactsListView.getItems().remove(selectedGoal); // Remove the selected goal from the ListView
        }
    }
}
