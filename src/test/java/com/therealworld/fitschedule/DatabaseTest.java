package com.therealworld.fitschedule;

import com.therealworld.fitschedule.model.FitScheduleDBConnection;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * General database unit tests to verify that the database behaves as expected, including
 * creation, insertion, updating, deletion, and other edge cases.
 */
public class DatabaseTest {

    private static File testSchedulingDatabaseFile;
    static Connection connection;

    /**
     * Setup before each test.
     * Initializes a fresh database connection, creates test tables, and inserts sample data.
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = FitScheduleDBConnection.getInstance();
        assertNotNull(connection, "Database connection should be initialized");

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS testTable (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "dayOfWeek VARCHAR(50) NOT NULL CHECK(LENGTH(dayOfWeek) <= 50) CHECK(dayOfWeek <> '') UNIQUE," +
                    "eventName VARCHAR(50)," +
                    "eventDescription VARCHAR(250)," +
                    "eventStartTime VARCHAR(50)," +
                    "eventEndTime VARCHAR(50))");

            statement.execute("INSERT INTO testTable (dayOfWeek) VALUES ('Wednesday'), ('Monday')");
        } catch (SQLException e) {
            fail("Error setting up test environment: " + e.getMessage());
        }
    }

    /**
     * Clean up after each test by dropping the test table.
     */
    @AfterEach
    void tearDown() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS testTable");
        } catch (SQLException e) {
            fail("Error cleaning up test environment: " + e.getMessage());
        }
    }

    /**
     * Close the database connection after all tests.
     */
    @AfterAll
    static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testConnection() {
        Connection conn = FitScheduleDBConnection.getInstance();
        assertNotNull(conn, "Database connection should not be null");
    }

    @Test
    void connectionToDatabaseShouldNotBeClosedOrNull() {
        assertDoesNotThrow(() -> {
            assertFalse(connection.isClosed(), "Connection should be open");
            assertNotNull(connection, "Connection should not be null");
        });
    }

    @Test
    void databaseFileIsCreated() {
        testSchedulingDatabaseFile = new File("testschedulingdatabase.db");
        assertTrue(testSchedulingDatabaseFile.exists(), "Database file should exist");
    }

    @Test
    void recordsCanBeInsertedIntoDatabaseTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO testTable (dayOfWeek) VALUES ('Friday')");
            try (ResultSet resultSet = statement.executeQuery("SELECT dayOfWeek FROM testTable WHERE dayOfWeek = 'Friday'")) {
                assertTrue(resultSet.next(), "Record should be found");
                assertEquals("Friday", resultSet.getString("dayOfWeek"), "Record should match 'Friday'");
            }
        }
    }

    @Test
    void recordsCanBeRetrieved() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id, dayOfWeek FROM testTable WHERE dayOfWeek = 'Monday'")) {
            assertTrue(resultSet.next(), "Record should be found");
            assertEquals("Monday", resultSet.getString("dayOfWeek"), "Day should match 'Monday'");
            assertEquals(2, resultSet.getInt("id"), "ID should match 2");
        }
    }

    @Test
    void recordsCanBeUpdated() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("UPDATE testTable SET dayOfWeek = 'Sunday' WHERE id = 1");
            try (ResultSet resultSet = statement.executeQuery("SELECT dayOfWeek FROM testTable WHERE id = 1")) {
                assertTrue(resultSet.next(), "Record should be found");
                assertEquals("Sunday", resultSet.getString("dayOfWeek"), "Day should match 'Sunday'");
            }
        }
    }

    @Test
    void recordsCanBeRemovedFromDatabaseTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM testTable WHERE dayOfWeek = 'Monday'");
            try (ResultSet resultSet = statement.executeQuery("SELECT dayOfWeek FROM testTable WHERE dayOfWeek = 'Monday'")) {
                assertFalse(resultSet.next(), "Record should be removed");
            }
        }
    }

    @Test
    void incorrectDataTypeCantBeInserted() {
        boolean exceptionThrown = false;
        try (Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO testTable (id, dayOfWeek) VALUES ('abc', 'Wednesday')");
        } catch (SQLException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown, "Data type mismatch exception expected");
    }

    @Test
    void cantInsertRecordsWithNullValuesIntoFieldsWithNotNullValues() {
        boolean exceptionThrown = false;
        try (Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO testTable (dayOfWeek) VALUES (null)");
        } catch (SQLException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown, "Null constraint exception expected");
    }

    @Test
    void throwExceptionIfDuplicateUniqueFieldsInserted() {
        boolean exceptionThrown = false;
        try (Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO testTable (dayOfWeek) VALUES ('Wednesday')");
            statement.execute("INSERT INTO testTable (dayOfWeek) VALUES ('Wednesday')");
        } catch (SQLException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown, "Unique constraint exception expected");
    }

    @Test
    void insertLargestPossibleLengthOfStringInFields() {
        boolean exceptionThrown = false;
        String maxLengthString = "e".repeat(50);
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO testTable (dayOfWeek) VALUES (?)")) {
            preparedStatement.setString(1, maxLengthString);
            preparedStatement.execute();
        } catch (SQLException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown, "String of max length should be allowed");
    }

    @Test
    void insertStringThatExceedsMaxLength() {
        boolean exceptionThrown = false;
        String exceedsMaxLength = "e".repeat(51);
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO testTable (dayOfWeek) VALUES (?)")) {
            preparedStatement.setString(1, exceedsMaxLength);
            preparedStatement.execute();
        } catch (SQLException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown, "String exceeding max length should cause exception");
    }
}
