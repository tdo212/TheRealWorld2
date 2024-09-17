package com.therealworld.fitschedule.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteGoalsDAO {

    private Connection connection;

    public SqliteGoalsDAO() {
        connection = SqliteConnection.getInstance();
        createTable();
    }

    // Create the goals table if it doesn't exist
    private void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS goals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "goal_type TEXT NOT NULL," +
                "goal_duration INTEGER NOT NULL," +  // Store goal duration as INTEGER for weeks
                "goal_period TEXT NOT NULL," +
                "goal_description TEXT," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert a new goal
    public void addGoal(int userId, String goalType, String goalDuration, int goalPeriod, int targetValue) {
        String query = "INSERT INTO goals (user_id, goal_type, goal_duration, goal_period, goal_description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, goalType);
            pstmt.setInt(3, goalPeriod);  // Save goal duration as INTEGER
            pstmt.setInt(4, targetValue);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fetch all goals for a specific user
    public List<String> getGoalsForUser(int userId) {
        List<String> goals = new ArrayList<>();
        String query = "SELECT * FROM goals WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                goals.add(rs.getString("goal_description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }

    // Delete a goal
    public void deleteGoal(int goalId) {
        String query = "DELETE FROM goals WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update a goal
    public void updateGoal(int goalId, String goalType, int goalPeriod, int targetValue, String goalDescription) {
        String query = "UPDATE goals SET goal_type = ?, goal_duration = ?, goal_period = ?, goal_description = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, goalType);
            pstmt.setInt(2, goalPeriod);  // Update goal duration as INTEGER
            pstmt.setInt(3, targetValue);
            pstmt.setString(4, goalDescription);
            pstmt.setInt(5, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
