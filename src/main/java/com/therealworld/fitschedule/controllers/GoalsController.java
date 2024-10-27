package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.Goal;
import com.therealworld.fitschedule.model.SqliteDAO;
import com.therealworld.fitschedule.model.UserSession;
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

import static com.therealworld.fitschedule.model.User.username;

/**
 * Controller class for managing goals, including displaying goals, badges, and progress tracking.
 */
public class GoalsController {

    @FXML
    private ListView<Goal> contactsListView; // ListView to display the list of goals
    @FXML
    private Label goalCountLabel; // Label to display the remaining goal count
    @FXML
    private Label goalCompletedLabel; // Label to display the session's completed goals count
    @FXML
    private PieChart pieChart; // PieChart to visualize goal completion status
    @FXML
    private ProgressBar progressBar; // ProgressBar to show progress towards goal completion
    @FXML
    private Label progressLabel; // Label to display the current progress percentage
    @FXML
    private Label UserIDLabel; // Label to display the user ID
    @FXML
    private ListView<String> badgesListView; // ListView to display earned badges
    @FXML
    private Label LifetimeCompleted; // Label to display lifetime goals completed count
    @FXML
    private Label goalCountLabel1; // Label to display the total goal count
    @FXML
    private PieChart pieChartLifetime; // PieChart for lifetime goal completion visualization
    @FXML
    private ProgressBar lifetimeProgressBar; // ProgressBar to display lifetime goal progress
    @FXML
    private Label progressLabel2; // Label to display lifetime progress percentage
    @FXML
    private Label UsernameLabel; // Label to display the username
    private SqliteDAO databaseHelper = new SqliteDAO(); // DAO for database interactions
    private int sessionGoalsCompleted = 0; // Tracks session's completed goals
    int userId = UserSession.getInstance().getUserId(); // User ID from session
    String username = databaseHelper.getUsernameById(userId); // Username fetched based on user ID

    /**
     * Initializes the UI components and loads user goal statistics.
     */
    public void initialize() {
        refreshGoalsList(); // Load the initial list of goals
        displayGoalCount();
        displayPieChart();
        updateProgressBar();
        refreshBadgesList();
        setStats();
        updatePieChart();
    }

    /**
     * Refreshes the list of goals and updates the UI to reflect changes in the goal list.
     */
    public void refreshGoalsList() {
        ObservableList<Goal> data = databaseHelper.getAllGoals(userId);

        System.out.println("Number of items to display: " + data.size());

        contactsListView.setItems(data); // Set goal data to ListView
        contactsListView.setCellFactory(param -> new ListCell<Goal>() {
            @Override
            protected void updateItem(Goal goal, boolean empty) {
                super.updateItem(goal, empty);
                if (empty || goal == null) {
                    setText(null);
                } else {
                    String goalDetails = String.format(
                            "ID: %d, Type: %s, Duration: %d weeks, Period: %s, Description: %s, Completed: %s",
                            goal.getGoalId(),
                            goal.getGoalType(),
                            goal.getGoalDuration(),
                            goal.getGoalPeriod(),
                            goal.getGoalDescription(),
                            goal.isGoalCompleted() ? "Yes" : "No"
                    );
                    setText(goalDetails);
                }
            }
        });

        displayGoalCount();
        displayPieChart();
        updateProgressBar();
        updateGoalsCompleted();
        updateLifeProgressBar();
        updatePieChart();
    }

    /**
     * Displays the count of remaining goals.
     */
    public void displayGoalCount() {
        int goalCount = databaseHelper.countGoalsRemaining(userId);
        goalCountLabel.setText("Goals Remaining: " + goalCount);
    }

    /**
     * Sets up user statistics, including total goals completed and other user-specific data.
     */
    public void setStats() {
        int totalGoalsCompleted = databaseHelper.getTotalGoalsCompleted(userId);
        int totalGoalCount = databaseHelper.countGoals();
        UserIDLabel.setText("User ID: " + userId);
        LifetimeCompleted.setText("Goals Completed (Lifetime): " + totalGoalsCompleted);
        goalCountLabel1.setText("Total Goals Completed: " + totalGoalCount);
        UsernameLabel.setText("Username: " + username);
    }

    /**
     * Updates the display for session goals completed.
     */
    public void updateGoalsCompleted() {
        goalCompletedLabel.setText("Goals Completed (Session): " + sessionGoalsCompleted);
    }

    /**
     * Checks the user's completed goals and awards badges for milestone completions.
     */
    public void checkBadges() {
        int totalGoalsCompleted = databaseHelper.getTotalGoalsCompleted(userId);

        if (totalGoalsCompleted == 0) {
            databaseHelper.awardBadge(userId, "New Beginnings: Account Created");
        }
        if (totalGoalsCompleted == 1) {
            databaseHelper.awardBadge(userId, "2 Goals Completed");
        }
        if (totalGoalsCompleted == 3) {
            databaseHelper.awardBadge(userId, "4 Goals Completed");
        }
        if (totalGoalsCompleted == 7) {
            databaseHelper.awardBadge(userId, "8 Goals Completed");
        }
        if (totalGoalsCompleted == 9) {
            databaseHelper.awardBadge(userId, "10 Goals Completed");
        }
        if (totalGoalsCompleted == 19) {
            databaseHelper.awardBadge(userId, "20 Goals Completed");
        }
    }

    /**
     * Displays a pie chart representing the completion status of goals.
     */
    public void displayPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        int goalCount = databaseHelper.countGoalsRemaining(userId);

        if (goalCount == 0 && sessionGoalsCompleted == 0) {
            pieChartData.add(new PieChart.Data("No Goals", 1));
        } else {
            pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Completed", sessionGoalsCompleted),
                    new PieChart.Data("Incomplete", goalCount)
            );
        }

        pieChart.setData(pieChartData); // Update pie chart with data
    }


    /**
     * Updates the session progress bar and label with the current completion percentage for goals.
     */
    public void updateProgressBar() {
        double goalRemaining = databaseHelper.countGoalsRemaining(userId); // Get remaining goals

        // Avoid division by zero
        if (goalRemaining <= 0 && completedGoals == 0) {
            progressBar.setProgress(0);
        } else if (goalRemaining > 0 && completedGoals > 0) {
            // Calculate progress as a double value
            double progressGoals = (double) completedGoals / (goalRemaining + completedGoals);

            // Set the progress bar value based on the calculated progress
            progressBar.setProgress(progressGoals);

            // Convert the progress value to a percentage and update the label
            double progressPercentage = progressGoals * 100;
            progressLabel.setText(progressPercentage + "%");
        } else if (goalRemaining == 0 && completedGoals >= 1) {
            progressBar.setProgress(1);
            progressLabel.setText("100%");
        }
    }

    /**
     * Updates the lifetime progress bar and label to show the cumulative completion rate for all goals.
     */
    public void updateLifeProgressBar() {
        double lifetimeRemaining = databaseHelper.countGoalsRemaining(userId); // Remaining lifetime goals
        double lifetimeCompleted = databaseHelper.getTotalGoalsCompleted(userId); // Completed lifetime goals

        // Calculate lifetime progress percentage
        double progressGoalsLifetime = (double) lifetimeCompleted / (lifetimeCompleted + lifetimeRemaining);

        // Set the lifetime progress bar and update the label
        lifetimeProgressBar.setProgress(progressGoalsLifetime);
        double progressPercentageLifetime = progressGoalsLifetime * 100;
        progressLabel2.setText(progressPercentageLifetime + "%");
    }

    /**
     * Updates the pie chart to display the current counts of completed and incomplete goals.
     */
    public void updatePieChart() {
        int goalsCompleted = databaseHelper.getCompletedGoalsCount(userId);
        int goalsRemaining = databaseHelper.countGoalsRemaining(userId);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Completed Goals", goalsCompleted),
                new PieChart.Data("Incomplete Goals", goalsRemaining)
        );
        pieChartLifetime.setData(pieChartData);
    }

    /**
     * Handles the action for the "Edit Goals" button, allowing users to modify goals in a new window.
     */
    @FXML
    public void onEditGoalsClick(ActionEvent event) {
        try {
            // Load the Edit Goals FXML file and display in a new window
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/therealworld/fitschedule/edit-goals-view.fxml"));
            Parent root = fxmlLoader.load();

            // Pass reference to current GoalsController instance to EditGoalsController
            EditGoalsController editGoalsController = fxmlLoader.getController();
            editGoalsController.setGoalsController(this);

            Stage stage = new Stage();
            stage.setTitle("Edit Goals");
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait until the edit goals window is closed

            // Refresh goals list after closing edit window
            refreshGoalsList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int completedGoals = 0; // New stat for tracking goals completed
    private int goalsProgress = 0;
    /**
     * Completes the selected goal, updates related progress metrics, and refreshes the UI.
     */
    @FXML
    public void onCompleteGoalsClick(ActionEvent event) {
        Goal selectedGoal = contactsListView.getSelectionModel().getSelectedItem();

        if (selectedGoal != null && !selectedGoal.isGoalCompleted()) {
            contactsListView.getItems().remove(selectedGoal); // Remove goal from ListView
            databaseHelper.updateGoalAsCompleted(selectedGoal.getGoalId()); // Mark goal as completed in DB
            completedGoals++;
            sessionGoalsCompleted++;
            goalsProgress++;

            updateGoalsCompleted(); // Refresh goals completed count
            updateProgressBar(); // Update progress bar
            displayPieChart(); // Update pie chart
            displayGoalCount();
            checkBadges(); // Check if badge should be awarded
            refreshBadgesList(); // Refresh badges display
            databaseHelper.initializeTotalGoalsCompleted(userId); // Ensure lifetime goals tracker exists
            databaseHelper.incrementTotalGoalsCompleted(userId); // Increment total goals completed in DB
            setStats(); // Update stats display
            updatePieChart();
            updateLifeProgressBar();
        }
    }

    /**
     * Deletes the selected goal from the database and updates the goal list in the UI.
     */
    @FXML
    public void onDeleteGoalsClick(ActionEvent event) {
        Goal selectedGoal = contactsListView.getSelectionModel().getSelectedItem();

        if (selectedGoal != null) {
            databaseHelper.deleteGoalFromDatabase(selectedGoal.getGoalId()); // Remove goal from DB

            // Refresh goals list after deletion
            refreshGoalsList();
        }
    }

    /**
     * Logs off the user and returns to the login screen.
     */
    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Refreshes the badge list view to display the user's latest earned badges.
     */
    public void refreshBadgesList() {
        ObservableList<String> badges = databaseHelper.getUserBadges(userId);
        badgesListView.setItems(badges);
    }
}
