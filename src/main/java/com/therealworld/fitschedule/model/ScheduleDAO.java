package com.therealworld.fitschedule.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    private final Connection connection;

    public ScheduleDAO() {
        connection = FitScheduleDBConnection.getInstance();  // Ensure the database connection is established
    }

    // Create table for the current schedule
    public void createTable() {
        try {
            Statement createTable = connection.createStatement();
            createTable.execute(
                    "CREATE TABLE IF NOT EXISTS currentSchedule ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "dayOfWeek VARCHAR(50) NOT NULL, "
                            + "eventName VARCHAR(50), "
                            + "eventDescription VARCHAR(250), "
                            + "eventStartTime VARCHAR(50), "
                            + "eventEndTime VARCHAR(50) "
                            + ")"
            );
        } catch (SQLException ex) {
            System.err.println("Error creating schedule table: " + ex.getMessage());
        }
    }

    // Insert a new schedule into the database
    public void insertSchedule(String dayOfWeek, String eventName, String eventDescription, String eventStartTime, String eventEndTime) {
        try {
            PreparedStatement insertSchedule = connection.prepareStatement(
                    "INSERT INTO currentSchedule (dayOfWeek, eventName, eventDescription, eventStartTime, eventEndTime) "
                            + "VALUES (?, ?, ?, ?, ?)"
            );
            insertSchedule.setString(1, dayOfWeek);
            insertSchedule.setString(2, eventName);
            insertSchedule.setString(3, eventDescription);
            insertSchedule.setString(4, eventStartTime);
            insertSchedule.setString(5, eventEndTime);
            insertSchedule.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error inserting schedule: " + ex.getMessage());
        }
    }

    // Update an existing schedule in the database
    public void update(Schedule currentSchedule) {
        try {
            PreparedStatement updateSchedule = connection.prepareStatement(
                    "UPDATE currentSchedule SET dayOfWeek = ?, eventName = ?, eventDescription = ?, eventStartTime = ?, eventEndTime = ? "
                            + "WHERE id = ?"
            );
            updateSchedule.setString(1, currentSchedule.getDayOfWeek());
            updateSchedule.setString(2, currentSchedule.getEventName());
            updateSchedule.setString(3, currentSchedule.getEventDescription());
            updateSchedule.setString(4, currentSchedule.getEventStartTime());
            updateSchedule.setString(5, currentSchedule.getEventEndTime());
            updateSchedule.setInt(6, currentSchedule.getId());  // Add condition to identify which record to update
            updateSchedule.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error updating schedule: " + ex.getMessage());
        }
    }

    // Delete a record from the schedule by its ID
    public void deleteRecord(int id) {
        try {
            PreparedStatement deleteRecord = connection.prepareStatement("DELETE FROM currentSchedule WHERE id = ?");
            deleteRecord.setInt(1, id);
            deleteRecord.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error deleting schedule: " + ex.getMessage());
        }
    }

    // Clear all schedules from the database (to generate a new schedule)
    public void generateNewSchedule() {
        try {
            PreparedStatement generateNewSchedule = connection.prepareStatement("DELETE FROM currentSchedule");
            generateNewSchedule.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error clearing schedules: " + ex.getMessage());
        }
    }

    // Retrieve all schedule data from the database
    public List<Schedule> getAll() {
        List<Schedule> currentSchedule = new ArrayList<>();
        try {
            Statement getAll = connection.createStatement();
            ResultSet rs = getAll.executeQuery("SELECT * FROM currentSchedule");

            // Loop through the result set and create Schedule objects
            while (rs.next()) {
                Schedule schedule = new Schedule(
                        rs.getInt("id"),
                        rs.getString("dayOfWeek"),
                        rs.getString("eventName"),
                        rs.getString("eventDescription"),
                        rs.getString("eventStartTime"),
                        rs.getString("eventEndTime")
                );
                currentSchedule.add(schedule);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving schedules: " + ex.getMessage());
        }
        return currentSchedule;
    }
}