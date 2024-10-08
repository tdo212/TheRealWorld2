package com.therealworld.fitschedule;

import com.therealworld.fitschedule.model.FitScheduleDBConnection;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is for general database unit tests
 * <p>
 * These tests verify that the database behaves as expected and includes
 * creation, insertion, updating and deletion test methods with appropriate edge cases and
 * conditional tests.
 * </p>
 */
public class DataTest {

    /**
     * The file object representing the database file to be created on the filesystem.
     */
    File testSchedulingDatabaseFile;
    /**
     * A single instance of the database connection.
     * This field is static so that there is only one single instance of the connection in the application.
     */
    private static Connection connection;
    /**
     * The JDBC URL used for the SQite database.
     * This field specifies the type of database, SQLite, and the path of the database file
     * "testschedulingdatabase.db".
     */
    String url = "jdbc:sqlite:testschedulingdatabase.db";

    /**
     * Creates a fresh database environment and database Table before each test.
     * <p>
     * This method is used for creating the database file if it doesn't already exist and
     * establishing a connection to the database. Upon connecting, a create statement is executed
     * to create a new test table used by the unit tests for a clean environment for isolated tests.
     * </p>
     *
     * @throws SQLException if there is an error connection to the database or creating the table.
     */
    @BeforeEach
    void setUp() throws SQLException {
        // Set up a fresh database for each test and initialize the test file
        testSchedulingDatabaseFile = new File("testschedulingdatabase.db");
        connection = DriverManager.getConnection(url);
        try (Statement createTable = connection.createStatement()) {
            // Adding UNIQUE constraint to 'dayOfWeek' to test for duplicates
            createTable.execute("CREATE TABLE IF NOT EXISTS testTable ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "dayOfWeek VARCHAR(50) NOT NULL CHECK(LENGTH(dayOfWeek) <= 50) CHECK(dayOfWeek <> '') UNIQUE,"
                    + "eventName VARCHAR(50),"
                    + "eventDescription VARCHAR(250), "
                    + "eventStartTime VARCHAR(50), "
                    + "eventEndTime VARCHAR(50) "
                    + ")"
            );
        }
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

    /**
     * This is a test method for ensuring the connection to the database is established and
     * is not closed or null.
     * <p>
     * Retrieves the database connection by getting the instance object of the database and
     * asserts that the both the connection made is not null and that the connection is not closed.
     *
     * @throws SQLException error and prints the error message to the terminal if the connection
     * cannot be established.
     * </p>
     */
    @Test
    void connectionToDatabaseShouldNotBeClosedOrNull() {
        try (Connection connection = FitScheduleDBConnection.getInstance()) {
            assertFalse(connection.isClosed());
            assertNotNull(connection);
        } catch (SQLException e) {
            System.err.println("Error connecting to database " + e.getMessage());
        }
    }

    /**
     * This is a test method for ensuring the database file is created, asserting that it exists
     * and is not null.
     *
     * <p>
     * Asserts that the data base test file is not null and that it does indeed exist, with a message
     * that says the file should exist.
     * </p>
     */
    @Test
    void databaseFileIsCreated() {
        // Verify that the database file was created
        assertNotNull(testSchedulingDatabaseFile);
        assertTrue(testSchedulingDatabaseFile.exists(), "Database file should exist.");
    }


    /**
     * This is a test method for ensuring a table can be created in the database, and if it is found to exist,
     * it is deleted.
     *
     * <p>
     * An SQL create statement is executed to create a temporary table in the database instance.
     * @throws SQLException if there is an error creating the table.
     *
     * A boolean value denotes the existence of the table by retrieving the return type from another
     * method "testIfTableExists", which attempts to select the name of the table created from the sqlite
     * database and if there is a value in the Result set, meaning that there are rows in the table created,
     * confirming it exists, it is returned as true, otherwise, it remains false.
     * @throws SQLException if there is an error finding the table in the database
     *
     * One last method is called to that drops the table if the boolean returned for denoting if the table
     * exists is true.
     * @throws SQLException if there is an error in the SQL statement to drop the table.
     * </p>
     */
    @Test
    void createTableAndIfExistsDeleteIt() throws SQLException {
        try (Statement createTable = connection.createStatement()) {
            createTable.execute("CREATE TABLE IF NOT EXISTS createTableTest (" + "id INTEGER PRIMARY KEY AUTOINCREMENT" + ")");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }

        boolean tableExists = testIfTableExists();
        deleteTableIfExists(tableExists);
    }

    boolean testIfTableExists() throws SQLException {
        boolean tableExists = false;
        try (Statement findTable = connection.createStatement()) {
            ResultSet findTableRecords = findTable.executeQuery("SELECT name FROM sqlite_master WHERE " +
                    "type='table' AND name='createTableTest'");
            tableExists = findTableRecords.next();
            assertTrue(tableExists);
        } catch (SQLException e) {
            System.err.println("Error finding table: " + e.getMessage());
        }
        return tableExists;
    }

    void deleteTableIfExists(boolean tableExists) {
        if (tableExists) {
            try (Statement dropTable = connection.createStatement()) {
                dropTable.execute("DROP TABLE IF EXISTS createTableTest");
            } catch (SQLException e) {
                System.err.println("Syntax Error in statement" + e.getMessage());
            }
        }
    }

    /**
     * This is a test method for ensuring that records can inserted into a table in the database.
     * <p>
     * Inserts 'Friday' into the 'dayOfWeek' column in the database table by executing an SQL statement.
     * Selects the data from the table that was just inserted to test if it exists. If a row is found
     * to exist in the table, it is asserted that the row must have the string value of 'Friday'
     * which was inserted.
     *
     * @throws SQLException if there is an error inserting the data into the table or fetching it after insertion.
     * </p>
     */
    @Test
    void recordsCanBeInsertedIntoDatabaseTable() throws SQLException {
        try (Statement insertRecords = connection.createStatement()) {
            insertRecords.execute("INSERT INTO testTable (dayOfWeek) VALUES ('Friday')");
            try (Statement findRecord = connection.createStatement();
                 ResultSet recordsExist = findRecord.executeQuery("SELECT dayOfWeek FROM testTable WHERE dayOfWeek = 'Friday'")) {
                if (recordsExist.next()) {
                    String recordResult = recordsExist.getString("dayOfWeek");
                    assertEquals("Friday", recordResult);
                }
            }
        }
    }

    /**
     * This is a test method for ensuring that records can retrieved from a table in the database.
     * <p>
     * Executes an SQL query to select an id and day of the week from table if the day of the week is a certain
     * value, e.g 'Monday'.
     *
     * If there is a valid row in the database table after retrieving it, it is asserted that result set
     * is consistent with the specific selected row in the table e.g containing the id of '2' and the
     * day of week as 'Monday'.
     *
     * @throws SQLException if there is an error retrieving the data.
     * </p>
     */
    @Test
    void recordsCanBeRetrieved() throws SQLException {
        try (Statement retrieveRecords = connection.createStatement();
             ResultSet recordRetrieval = retrieveRecords.executeQuery("SELECT id, dayOfWeek FROM testTable WHERE dayOfWeek = 'Monday'")) {
            if (recordRetrieval.next()) {
                String retrievedDayOfWeek = recordRetrieval.getString("dayOfWeek");
                int retrieveId = recordRetrieval.getInt("id");
                assertEquals("Monday", retrievedDayOfWeek);
                assertEquals(2, retrieveId);
            }
        }
    }

    /**
     * This is a test method for testing an edge case of attempting to retrieve a record that is null
     * in a database table.
     * <p>
     * Executes an SQL query to select an id that shouldn't exist in the table.
     * If the result set returns false from no rows being found, then a boolean value for
     * 'recordNotFound' is changed to true and is asserted to be true to ensure that the null record was not
     * in the result set.
     *
     * @throws SQLException for the error produce from retrieving a record in table that doesn't exist.
     * </p>
     */
    @Test
    void retrieveNullRecordInTable() throws SQLException {
        boolean recordNotFound = false;
        try (Statement retrieveNullRecord = connection.createStatement();
             ResultSet nullRecordRetrieval = retrieveNullRecord.executeQuery("SELECT id FROM testTable WHERE id = 20")) {
            if (!nullRecordRetrieval.next()) {
                recordNotFound = true;
            }
        }
        assertTrue(recordNotFound);
    }

    /**
     * This is a test method for testing that records in a database table can be updated.
     * <p>
     * Executes an SQL query to update the table to set the day of the week as 'Sunday' where the id
     * of the record is '1'.
     *
     * A query is executed to select the record with the updated fields, and if the result set denotes
     * that the updated row does exist, it is asserted that the fields for the particular row is consistent
     * with the updated changes of 'Sunday' and the id as '1'.
     *
     * @throws SQLException if there is an error updating the record or the updated record cannot be found.
     * </p>
     */
    @Test
    void recordsCanBeUpdated() throws SQLException {
        try (Statement updateRecords = connection.createStatement()) {
            updateRecords.execute("UPDATE testTable SET dayOfWeek = 'Sunday' WHERE id = 1;");
            try (Statement retrieveUpdatedRecords = connection.createStatement();
                 ResultSet updatedRecords = retrieveUpdatedRecords.executeQuery("SELECT id, dayOfWeek FROM testTable WHERE dayOfWeek='Sunday' AND id=1")) {
                if (updatedRecords.next()) {
                    String retrievedDayOfWeek = updatedRecords.getString("dayOfWeek");
                    int retrieveId = updatedRecords.getInt("id");
                    assertEquals("Sunday", retrievedDayOfWeek);
                    assertEquals(1, retrieveId);
                }
            }
        }
    }

    /**
     * This is a test method to test under the conditions of inserting incorrect data types for columns
     * in a database table.
     * <p>
     * Executes an SQL insert statement to intentionally insert a string value into the 'id' column which
     * is integer only.
     *
     * @throws SQLException for the consequent data type mismatch error cause due to the test and prints
     * the error to the terminal.
     *
     * A boolean value is changed to true based on the error denoting an incorrect data type was inserted.
     *
     * This error is expected and the boolean is asserted as true.
     * </p>
     */
    @Test
    void incorrectDataTypeCantBeInserted() {
        boolean incorrectDataTypeInserted = false;
        try (Statement insertRecords = connection.createStatement()) {
            // Attempt to insert a string into the 'id' column which expects an integer
            insertRecords.execute("INSERT INTO testTable (id, dayOfWeek) VALUES ('abc', 'Wednesday')");
        } catch (SQLException e) {
            System.err.println("Can't insert record with invalid data type: " + e.getMessage());
            incorrectDataTypeInserted = true;
        }
        assertTrue(incorrectDataTypeInserted, "Expected a data type mismatch error, but no error occurred.");
    }

    /**
     * This is a test method to test under the conditions of inserting a duplicate unique field into
     * a database table.
     * <p>
     * Executes an SQL insert statement to intentionally insert two rows with the same day of the week of
     * which has a 'unique' constraint.
     *
     * @throws SQLException for the consequent error caused by inserting two of the same 'unique' values.
     *
     * A boolean value is changed to true based on the error.
     *
     * This error is expected and the boolean is asserted as true.
     * </p>
     */
    @Test
    void throwExceptionIfDuplicateUniqueFieldsInserted() {
        boolean isThereADuplicateRecord = false;
        try (Statement insertRecords = connection.createStatement()) {
            // Insert first record with dayOfWeek = 'Wednesday'
            insertRecords.execute("INSERT INTO testTable (dayOfWeek) VALUES ('Wednesday')");
            // Attempt to insert a duplicate 'dayOfWeek' value, which should fail due to UNIQUE constraint
            insertRecords.execute("INSERT INTO testTable (dayOfWeek) VALUES ('Wednesday')");
        } catch (SQLException e) {
            System.err.println("Can't insert record with duplicate 'dayOfWeek': " + e.getMessage());
            isThereADuplicateRecord = true;
        }
        assertTrue(isThereADuplicateRecord, "Expected a duplicate 'dayOfWeek' error, but no error occurred.");
    }

    /**
     * This is a test method to test under the conditions of inserting data into the incorrect column
     * name in a database table.
     * <p>
     * Executes an SQL insert statement to intentionally insert data into a misspelled column name that
     * doesn't exist in the table.
     *
     * @throws SQLException for the consequent error caused by inserting into a non-existent column and
     * prints the error to the terminal.
     *
     * A boolean value for the column name being correct is changed to false due to the error.
     *
     * This error is expected and the boolean is asserted as false,
     * </p>
     */
    @Test
    void testInsertIncorrectColumnName() {
        boolean isColumnNameCorrect = true;
        String errorMessage = "";
        try (Statement insertRecords = connection.createStatement()) {
            // Correct column name is `dayOfWeek`, not `daofWeek`
            insertRecords.execute("INSERT INTO testTable (daofWeek) VALUES ('Saturday')");
        } catch (SQLException e) {
            errorMessage = e.getMessage();
            System.err.println("Can't insert into non-existent column: " + errorMessage);
            isColumnNameCorrect = false;
        }
        assertFalse(isColumnNameCorrect, "Expected column name issue, but no error occurred.");
        assertTrue(errorMessage.contains("no such column: daofWeek") || errorMessage.contains("SQL error or missing database"),
                "Expected error message indicating missing column, but got: " + errorMessage);
    }

    /**
     * Closes the connection to the database after all tests are completed.
     * <p>
     * Ensures there is a connection to the database before closing it.
     *
     * @throws SQLException if there is no connection or the connection is being closed while changes are
     * being made to the database.
     * </p>
     */
    @AfterAll
    static void removeTestDatabase() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
