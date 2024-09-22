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
    private void onEditGoalsClick(ActionEvent event) {
        try {
            // Load the Edit Goals FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/therealworld/fitschedule/edit-goals-view.fxml"));
            Parent root = fxmlLoader.load();

            // Create a new stage (window) for the Edit Goals UI
            Stage stage = new Stage();
            stage.setTitle("Edit Goals");
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait until the edit goals window is closed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private ListView<String> contactsListView;
    @FXML
    private Label goalCountLabel; // Label to display goal count
    @FXML
    private Label goalCompletedLabel; // Label to display goal count
    private SqliteDAO databaseHelper = new SqliteDAO();
    private EditGoalsController editGoalsController = new EditGoalsController();
    public void initialize() {
        ObservableList<String> data = SqliteDAO.getAllGoals();
        System.out.println("Number of items to display: " + data.size());
        contactsListView.setItems(data);
        displayGoalCount();
        displayPieChart();
        updateProgressBar();
    }
    public void displayGoalCount() {
        int goalCount = databaseHelper.countGoals();
        goalCountLabel.setText("Goals Remaining: " + goalCount);

    }
    int goalsCompleted = 0;
    public void updateGoalsCompleted() {
        int goalCount = databaseHelper.countGoals();
        goalsCompleted = goalsCompleted + 1;


        goalCompletedLabel.setText("Goals Completed: " + goalsCompleted);
    }
    @FXML
    private PieChart pieChart;
    public void displayPieChart() {
        int goalCount = databaseHelper.countGoals();
        // Sample data for the PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Completed", goalsCompleted),
                new PieChart.Data("Incomplete", goalCount)

        );

        // Set data to the PieChart
        pieChart.setData(pieChartData);
    }

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;  // Label to display percentage
    // Method to calculate and update the progress bar
    public void updateProgressBar() {
        // Get the completed and total goal stats from the database
        int goalCount = databaseHelper.countGoals();
        double progressgoals = (double) goalsCompleted / goalCount;
        progressBar.setProgress(progressgoals);
        int progresslabel = (int) (progressgoals * 100);
        progressLabel.setText(progresslabel + "%");
    }
    @FXML
    public void onCompleteGoalsClick(ActionEvent event) {
        String selectedGoal = contactsListView.getSelectionModel().getSelectedItem();

        contactsListView.getItems().remove(selectedGoal);
        updateGoalsCompleted();
        updateProgressBar();
        displayPieChart();

    }

    @FXML
    public void onDeleteGoalsClick(ActionEvent event) {
        String selectedGoal = contactsListView.getSelectionModel().getSelectedItem();

        contactsListView.getItems().remove(selectedGoal);

    }
}