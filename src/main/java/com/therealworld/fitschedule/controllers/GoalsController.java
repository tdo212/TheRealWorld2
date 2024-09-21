package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    private DatabaseHelper databaseHelper = new DatabaseHelper();
    public void initialize() {
        ObservableList<String> data = DatabaseHelper.getAllGoals();
        System.out.println("Number of items to display: " + data.size());
        contactsListView.setItems(data);
        displayGoalCount();
        displayPieChart();
    }
    public void displayGoalCount() {
        int goalCount = databaseHelper.countGoals();
        goalCountLabel.setText("Goals Remaining: " + goalCount);
    }
    @FXML
    private PieChart pieChart;
    public void displayPieChart() {
        // Sample data for the PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Category A", 50),
                new PieChart.Data("Category B", 30),
                new PieChart.Data("Category C", 20)
        );

        // Set data to the PieChart
        pieChart.setData(pieChartData);
    }
}
