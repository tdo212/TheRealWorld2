package com.therealworld.fitschedule.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseHelper {

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
}
