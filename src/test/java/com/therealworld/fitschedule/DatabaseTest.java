package com.therealworld.fitschedule;

import com.therealworld.fitschedule.model.FitScheduleDBConnection;  // Import the correct connection class
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseTest {

    @Test
    public void testConnection() {
        // Use the correct method to get the database connection instance
        Connection conn = FitScheduleDBConnection.getInstance();
        // Check if the connection is not null, which means it was established successfully
        assertNotNull(conn, "Database connection should not be null");
    }
}
