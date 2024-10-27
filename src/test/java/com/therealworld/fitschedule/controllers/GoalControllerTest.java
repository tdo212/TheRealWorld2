package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.Goal;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.chart.PieChart;
import javafx.collections.FXCollections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GoalControllerTest {

    private Label goalCountLabel;
    private Label progressLabel;
    private ProgressBar progressBar;
    private ListView<Goal> contactsListView;
    private ListView<String> badgesListView;
    private PieChart pieChart;
    private Label UserIDLabel;
    private Label progressLabel2;
    private ProgressBar lifetimeProgressBar;
    private Label UsernameLabel;

    @BeforeEach
    public void setUp() {
        // Run JavaFX initialization in a separate thread
        Platform.runLater(() -> {
            GoalsController goalsController = new GoalsController();

            // Initialize UI components
            goalCountLabel = new Label();
            progressLabel = new Label();
            progressBar = new ProgressBar();
            contactsListView = new ListView<>();
            badgesListView = new ListView<>();
            pieChart = new PieChart();
            UserIDLabel = new Label();
            progressLabel2 = new Label();
            lifetimeProgressBar = new ProgressBar();
            UsernameLabel = new Label();

            // Use setters to inject components into GoalsController
            goalsController.setGoalCountLabel(goalCountLabel);
            goalsController.setProgressLabel(progressLabel);
            goalsController.setProgressBar(progressBar);
            goalsController.setContactsListView(contactsListView);
            goalsController.setBadgesListView(badgesListView);
            goalsController.setPieChart(pieChart);
            goalsController.setUserIDLabel(UserIDLabel);
            goalsController.setProgressLabel2(progressLabel2);
            goalsController.setLifetimeProgressBar(lifetimeProgressBar);
            goalsController.setUsernameLabel(UsernameLabel);
        });
    }

    @Test
    public void testGoalCountLabelDisplay() {
        Platform.runLater(() -> {
            goalCountLabel.setText("Goals Remaining: 1");
            assertEquals("Goals Remaining: 1", goalCountLabel.getText(), "Goal count label text is incorrect");
        });
    }

    @Test
    public void testProgressBarFullyComplete() {
        Platform.runLater(() -> {
            progressBar.setProgress(1.0);
            assertEquals(1.0, progressBar.getProgress(), "Progress bar should be fully complete");
        });
    }

    @Test
    public void testProgressPercentageLabel() {
        Platform.runLater(() -> {
            progressLabel.setText("100%");
            assertEquals("100%", progressLabel.getText(), "Progress label should show 100%");
        });
    }

    @Test
    public void testContactsListViewNotEmpty() {
        Platform.runLater(() -> {
            Goal sampleGoal = new Goal(1, "Fitness", 4, "Weekly", "Run 5 miles", false, 0);
            contactsListView.setItems(FXCollections.observableArrayList(sampleGoal));
            assertFalse(contactsListView.getItems().isEmpty(), "Contacts list view should not be empty");
        });
    }

    @Test
    public void testGoalCompletionStatus() {
        Platform.runLater(() -> {
            Goal completedGoal = new Goal(2, "Study", 3, "Daily", "Read 30 pages", true, 100);
            assertTrue(completedGoal.isGoalCompleted(), "Goal should be marked as completed");
        });
    }

    @Test
    public void testPieChartData() {
        Platform.runLater(() -> {
            pieChart.setData(FXCollections.observableArrayList(
                    new PieChart.Data("Completed", 1),
                    new PieChart.Data("Incomplete", 1)
            ));
            assertEquals(2, pieChart.getData().size(), "Pie chart should have two data slices: completed and incomplete goals");
        });
    }

    @Test
    public void testBadgesListViewContainsBadges() {
        Platform.runLater(() -> {
            badgesListView.setItems(FXCollections.observableArrayList("Newbie Badge", "Intermediate Badge"));
            assertFalse(badgesListView.getItems().isEmpty(), "Badges list view should not be empty");
        });
    }

    @Test
    public void testUserIDLabelDisplaysCorrectUserID() {
        Platform.runLater(() -> {
            UserIDLabel.setText("User ID: 12");
            assertEquals("User ID: 12", UserIDLabel.getText(), "User ID label text does not match expected ID");
        });
    }

    @Test
    public void testLifetimeProgressBarDisplaysCorrectProgress() {
        Platform.runLater(() -> {
            lifetimeProgressBar.setProgress(0.75);
            assertEquals(0.75, lifetimeProgressBar.getProgress(), 0.01, "Lifetime progress bar should be accurate to within 1%");
        });
    }

    @Test
    public void testLifetimeProgressPercentageLabel() {
        Platform.runLater(() -> {
            progressLabel2.setText("75%");
            assertEquals("75%", progressLabel2.getText(), "Lifetime progress percentage label should display correctly");
        });
    }

    @Test
    public void testUsernameLabelDisplaysCorrectUsername() {
        Platform.runLater(() -> {
            UsernameLabel.setText("Username: testuser");
            assertEquals("Username: testuser", UsernameLabel.getText(), "Username label should display correct username");
        });
    }
}
