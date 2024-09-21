package com.therealworld.fitschedule.model;

import java.sql.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseHelper {

    // Existing method to get the connection
    private Connection connect() {
        String url = "jdbc:sqlite:fitschedule.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static ObservableList<String> getAllGoals() {
        ObservableList<String> data = FXCollections.observableArrayList();
        String url = "jdbc:sqlite:fitschedule.db"; // Make sure this path is correct

        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("Database connection established.");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM goals");

            while (rs.next()) {
                // Construct a string with all the columns in the goals table
                String goalEntry = String.format(
                        "ID: %d, User ID: %d, Type: %s, Duration: %d, Period: %s, Description: %s",
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("goal_type"),
                        rs.getInt("goal_duration"),
                        rs.getString("goal_period"),
                        rs.getString("goal_description")
                );
                System.out.println("Fetched goal entry: " + goalEntry);
                data.add(goalEntry);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Data loaded: " + data.size() + " items.");
        return data;
    }
    public int countGoals() {
        String sql = "SELECT COUNT(*) AS count FROM goals";
        int count = 0;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return count;
    }

}







