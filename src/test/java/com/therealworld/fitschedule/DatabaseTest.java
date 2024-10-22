package com.therealworld.fitschedule;

import com.therealworld.fitschedule.model.FitScheduleDBConnection;  // Import the correct connection class
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.therealworld.fitschedule.DataTest.connection;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseTest {

    @Test
    public void testConnection() {
        // Use the correct method to get the database connection instance
        Connection conn = FitScheduleDBConnection.getInstance();
        // Check if the connection is not null, which means it was established successfully
        assertNotNull(conn, "Database connection should not be null");
    }

    /**
     * Removes the test table from the test database after each test.
     * <p>
     * This method is used for isolating tests and test data so that they don't depend on the results
     * of previous tests affecting data in the database table. An SQL statement is executed to drop
     * the table if it exists in the database after every test.
     *
     * @throws SQLException if an error occurs while attempting to delete the table.
     * </p>
     */
    @AfterEach
    void tearDown() throws SQLException {
        try (Statement dropTable = connection.createStatement()) {
            dropTable.execute("DROP TABLE IF EXISTS testTable");
        }
    }
}



