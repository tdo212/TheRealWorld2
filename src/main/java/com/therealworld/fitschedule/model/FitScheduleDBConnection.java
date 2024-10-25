package com.therealworld.fitschedule.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FitScheduleDBConnection {
    private static Connection instance = null;

    // Private constructor to prevent direct instantiation
    private FitScheduleDBConnection() {}

    public static synchronized Connection getInstance() {
        if (instance == null) {
            String url = "jdbc:sqlite:FitScheduleDBConnection.db";  // Remove spaces in the file name
            try {
                instance = DriverManager.getConnection(url);
            } catch (SQLException sqlEx) {
                System.err.println("Error establishing database connection: " + sqlEx.getMessage());
                // Optionally rethrow the exception or handle it in a better way
            }
        }
        return instance;
    }
}
