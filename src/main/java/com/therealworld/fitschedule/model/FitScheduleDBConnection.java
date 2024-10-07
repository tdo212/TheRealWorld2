package com.therealworld.fitschedule.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class responsible for managing the database connection for the FitSchedule application.
 * Provides a single, globally accessible instance of the database connection, ensuring efficient
 * use of resources and consistent access across different parts of the application.
 */
public class FitScheduleDBConnection {

    /**
     * The single instance of the database connection.
     */
    private static Connection instance = null;

    /**
     * Private constructor to prevent direct instantiation.
     * Enforces the singleton pattern to ensure only one connection instance is created.
     */
    private FitScheduleDBConnection() {}

    /**
     * Provides access to the single instance of the database connection.
     * If the connection has not been established yet, it initializes a new connection.
     *
     * @return the single `Connection` instance for the application's database.
     */
    public static synchronized Connection getInstance() {
        if (instance == null) {
            String url = "jdbc:sqlite:FitScheduleDBConnection.db";  // Database URL for SQLite connection
            try {
                instance = DriverManager.getConnection(url);
            } catch (SQLException sqlEx) {
                System.err.println("Error establishing database connection: " + sqlEx.getMessage());
                // Optional: Rethrow the exception or implement custom handling logic here
            }
        }
        return instance;
    }
}
