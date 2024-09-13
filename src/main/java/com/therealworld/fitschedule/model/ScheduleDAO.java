package com.therealworld.fitschedule.model;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    private Connection connection;

    public ScheduleDAO() {
        connection = FitScheduleDBConnection.getInstance();
    }


//    May have to Possibly add Date here as well
//    All data are Varchars for simplicity, we can change them to their
//    necessary Data types later

//    Creating table for current schedule
public void createTable() {
        try {
            Statement createTable = connection.createStatement();
            createTable.execute(
                    "CREATE TABLE IF NOT EXISTS currentSchedule ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "dayOfWeek VARCHAR(50) NOT NULL, "
                            + "eventName VARCHAR(50), "
                            + "eventDescription VARCHAR(250), "
                            + "eventStartTime VARCHAR(50),  "
                            + "eventEndTime VARCHAR(50) "
                            + ")"
            );
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }


//    Update Schedule

    public void update(Schedule currentSchedule) {
        try {
            PreparedStatement updateSchedule = connection.prepareStatement(
                    "UPDATE schedules SET dayOfWeek = ?, eventName = ?, eventDescription = ?, eventStartTime = ?, eventEndTime = ? WHERE id = ?"
            );
            updateSchedule.setString(1, currentSchedule.getDayOfWeek());
            updateSchedule.setString(2, currentSchedule.getEventName());
            updateSchedule.setString(3, currentSchedule.getEventDescription());
            updateSchedule.setString(4, currentSchedule.getEventStartTime());
            updateSchedule.setString(5, currentSchedule.getEventEndTime());
            updateSchedule.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    public void deleteRecord(int id) {
        try {
            PreparedStatement deleteRecord = connection.prepareStatement("DELETE FROM Currentschedule WHERE id = ?");
            deleteRecord.setInt(1, id);
            deleteRecord.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

    //    Deletes all data from current schedule table to generate a new one
    public void generateNewSchedule(){
        try {
            PreparedStatement generateNewSchedule = connection.prepareStatement("DELETE FROM Currentschedule");
            generateNewSchedule.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }

    }

//    Get all data from schedule
    public List<Schedule> getAll() {
        List<Schedule> currentSchedule = new ArrayList<>();
        try {
            Statement getAll = connection.createStatement();
            ResultSet rs = getAll.executeQuery("SELECT * FROM schedules");
            while (rs.next()) {
                currentSchedule.add(
                        new Schedule(
                                rs.getString("dayOfWeek"),
                                rs.getString("eventName"),
                                rs.getString("eventDescription"),
                                rs.getString("eventStartTime"),
                                rs.getString("eventEndTime")
                        )
                );
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return currentSchedule;
    }

    //    Add a schedule into the Database
    public void insertSchedule(String dayOfWeek, String eventName, String eventDescription, String eventStartTime, String eventEndTime) {
        try {
            PreparedStatement insertSchedule = connection.prepareStatement(
                    "INSERT INTO currentSchedule (dayOfWeek, eventName, eventDescription, eventStartTime, eventEndTime) VALUES (?, ?, ?, ?, ?)"
            );
            insertSchedule.setString(1, dayOfWeek);
            insertSchedule.setString(2, eventName);
            insertSchedule.setString(3, eventDescription);
            insertSchedule.setString(4, eventStartTime);
            insertSchedule.setString(5, eventEndTime);
            insertSchedule.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
    }

}