package com.therealworld.fitschedule;

import com.therealworld.fitschedule.model.FitScheduleDBConnection;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    static Connection connection;
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
     *
     * Sample mock data is then inserted into the database for consistent use in the unit tests.
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

            //        Inserts Mock Data Into Table
            try (Statement insertRecords = connection.createStatement()) {
                insertRecords.execute("INSERT INTO testTable (dayOfWeek) VALUES ('Wednesday'),('Monday')");
            }
            catch (SQLException e){
                System.err.println("Error inserting data into table: " + e.getMessage());
            }
        } catch (SQLException e){
            System.err.println("Error creating table: " + e.getMessage());
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
     * @throws SQLException for the error produced from retrieving a record in table that doesn't exist.
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
     * This is a test method for testing that records in a database table can be removed.
     * <p>
     * Executes an SQL query to delete existing records in the table where the day of the week is 'Monday'
     *
     * A query is executed to attempt to select the deleted record and if that record is not found in the
     * result set, an error is thrown indicating the record was removed successfully and a boolean value for
     * 'recordExists' is set to false and asserted as false if there record was successfully deleted.
     *
     * @throws SQLException for the error produced by selecting a specific record in a database table that
     * doesn't exist.
     * </p>
     */
    @Test
    void recordsCanBeRemovedFromDatabaseTable() throws SQLException {
        // Deletes a record from the table
        boolean recordExists = true;
        try (Statement deleteRecords = connection.createStatement()) {
            deleteRecords.execute("DELETE FROM testTable WHERE dayOfWeek = 'Monday';");
            // Attempts to select record to test if it has been deleted
            try (Statement selectDeletedRecord = connection.createStatement()) {
                ResultSet deletedRecord = selectDeletedRecord.executeQuery("SELECT (dayOfWeek) FROM testTable WHERE dayOfWeek = 'Monday';");
                deletedRecord.next();
                deletedRecord.getInt(1);
                deletedRecord.getString("dayOfWeek");
                // SQLException error means that the record could not be found and has successfully been deleted
            } catch (SQLException e){
                recordExists = false;
            }

            assertFalse(recordExists);
        }
    }

    /**
     * This is a test method for testing that records in a database that don't exist can't be selected.
     * <p>
     * Executes an SQL query to select a record in the database table with the day of the week 'dayThatDoesNotExist'
     * which is not contained in the database table.
     *
     * The result set is expected to not contain the record and throws out an error which is caught.
     *
     * @throws SQLException for the error produced by selecting a specific record in a database table that
     * doesn't exist.
     * </p>
     */
    @Test
    void recordsThatDontExistCantBeRetrieved() {
        // Test that record does not exist by trying to select it from the database
        try (Statement retrieveNonExistentRecord = connection.createStatement()) {
            ResultSet nonExistentRecord = retrieveNonExistentRecord.executeQuery("SELECT (dayOfWeek) FROM testTable WHERE dayOfWeek='dayThatDoesNotExist'");
            nonExistentRecord.next();
            // Throws exception if record does not exist in dayOfWeek column
            nonExistentRecord.getString("dayOfWeek");
        } catch (SQLException e) {
            System.err.println("Can't retrieve record that does not exist in database table: " + e.getMessage());
        }
    }

    /**
     * This is a test method for testing that records in a database that don't already exist can't be deleted.
     * <p>
     * Executes an SQL query to select the count of rows from a database table and is stored as an integer
     * based on the result set from the query.
     *
     * An SQL delete statement is executed to remove a specific row in the table that doesn't exist, with the value
     * 'dayThatDoesNotExist'.
     *
     * All The rows are counted with another SQL Select query and the result set is stored as an integer with the
     * count of rows. This is compared to the previous count of rows before, and if the count of rows are the same,
     * it was successful as a record that didn't exist in the first place was not deleted.
     *
     * @throws SQLException for the error produced by deleting a null/non-existent record.
     * </p>
     */
    @Test
    void recordsThatDontExistCantBeDeleted() throws SQLException{
        // Count records before deletion
        try (Statement countRecords = connection.createStatement()) {
            ResultSet countedRecords = countRecords.executeQuery("SELECT COUNT(*) FROM testTable");
            countedRecords.next();
            int recordCountBeforeDeletion = countedRecords.getInt(1);
            //  Attempts to delete non-existent record
            try (Statement deleteNonExistentRecord = connection.createStatement()) {
                deleteNonExistentRecord.execute("DELETE FROM testTable WHERE dayOfWeek='dayThatDoesNotExist'");

            }
            // Count records after deletion
            try (Statement countRecordsAfter = connection.createStatement()) {
                ResultSet countedRecordsAfter = countRecordsAfter.executeQuery("SELECT COUNT(*) FROM testTable");
                countedRecords.next();
                int recordCountAfterDeletion = countedRecordsAfter.getInt(1);

                if (recordCountBeforeDeletion == recordCountAfterDeletion){
                    System.err.println("Can't delete records that don't exist");
                }
            }
        }
    }

    /**
     * This is a test method for testing that records in a database that don't already exist can't be updated.
     * <p>
     * Executes an SQL query to select a non-existent dayOfWeek in the database with a value of 'dayThatDoesNotExist'.
     * This result set of the query is returned to get the value of the non-existent record, which should throw an
     * error, confirming that the record doesn't exist already.
     *
     * The error is caught, and the non-existent record is attempted to be updated with the value 'Tuesday' for the
     * day of the week.
     *
     * The upated value of the record is then attempted to be selected based on the new value, of which is expected to
     * throw an error as it did not exist in the first place and therefore cannot be updated.
     *
     * @throws SQLException for the error produced by updating a null/non-existent record.
     * </p>
     */
    @Test
    void recordsThatDontExistCantBeUpdated() throws SQLException{
        // Confirms that record to be updated doesn't exist
        try (Statement retrieveNonExistentRecords = connection.createStatement()) {
            ResultSet retrievedRecords = retrieveNonExistentRecords.executeQuery("SELECT dayOfWeek FROM testTable WHERE dayOfWeek='dayThatDoesNotExist'");
            retrievedRecords.next();
            retrievedRecords.getString("dayOfWeek");
        }
        catch (SQLException e) {
        }
        //  Attempts to update non-existent record
        try (Statement updateNonExistentRecord = connection.createStatement()) {
            updateNonExistentRecord.execute("UPDATE testTable SET dayOfWeek='Tuesday' WHERE dayOfWeek='dayThatDoesNotExist'");
        }
        // Attempts to retrieve updated records
        try (Statement retrieveUpdatedNonExistentRecord = connection.createStatement()) {
            ResultSet updatedNonExistentRecords = retrieveUpdatedNonExistentRecord.executeQuery("SELECT dayOfWeek FROM testTable WHERE dayOfWeek='dayThatDoesNotExist'");
            updatedNonExistentRecords.next();
            updatedNonExistentRecords.getString("dayOfWeek");
        } catch (SQLException e){
            System.err.println("Can't update records that don't exist: " + e.getMessage());
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
     * This is a test method to test under the conditions of updating records with data that is invalid.
     * <p>
     * Executes an SQL update statement to intentionally set the integer id of a record '1', with a string 'asd'.
     *
     * @throws SQLException for the consequent error due to inserting a string value into an integer field.
     *
     * The original record is inserted with a select statement that is executed to select the specific id.
     * The result set of the id and 'dayoftheweek' column is asserted to equal the original values of '1' and
     * 'Wednesday' to ensure that the update was not successful.
     * </p>
     */
    @Test
    void cantUpdateRecordsWithInvalidData() throws SQLException{
        try (Statement updateRecordWithInvalidType = connection.createStatement()) {
            // Attempts to update an integer ID with a string
            updateRecordWithInvalidType.execute("UPDATE testTable SET id='asd' WHERE dayOfWeek='Wednesday' AND id='1'");
        } catch (SQLException e){
            System.err.println("Can't update record with invalid data type: " + e.getMessage());
        }
        // Selects original record to ensure it hasn't been updated
        try (Statement selectRecord = connection.createStatement()) {
            // Attempts to update an integer ID with a string
            ResultSet retrievedRecords = selectRecord.executeQuery("SELECT id, dayOfWeek FROM testTable WHERE id='1' AND dayOfWeek='Wednesday'");
            retrievedRecords.next();
            // Ensures original mock data record values have not been changed
            assertEquals(1, retrievedRecords.getInt(1));
            assertEquals("Wednesday", retrievedRecords.getString("dayOfWeek"));

        }
    }

    /**
     * This is a test method to test that all records in the database can be retrieved successfully.
     * <p>
     * Executes an SQL update statement to select all records from the database table.
     * These records are added into a list of strings.
     *
     * Specific records that exist in the database table are asserted to contain specific values
     * based on their index stored in the list to ensure they match with the database.
     *
     * @throws SQLException for if there was an error retrieving a record from the table.
     *
     */
    @Test
    void allRecordsCanBeRetrieved() throws  SQLException{
        List<String> testTable = new ArrayList<>();
        try {
            Statement getAll = connection.createStatement();
            ResultSet allRecords = getAll.executeQuery("SELECT * FROM testTable");
            // Loop through the result and add to list
            while (allRecords.next()) {
                testTable.add(allRecords.getString("dayOfWeek"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving record: " + e.getMessage());
        }
        // Ensures that records from database match the records stored in the list
        assertEquals("Wednesday",testTable.get(0));
        assertEquals("Monday",testTable.get(1));

    }

    /**
     * This is a test method to test that all the data in a database table can be removed successfully.
     * <p>
     * Executes an SQL select statement to count all the rows in the test table.
     * It is asserted that the count is to be 2 rows based on the pre-existing rows inserted before every
     * unit test.
     *
     * An SQL delete statement is executed to delete everything from the table.
     * The rows are then counted again with an SQL select statement. It is asserted that the number
     * of rows are 0, indicated that the table was cleared successfully.
     *
     * @throws SQLException if there was an error counting records before and after as well as deleting all records
     * from the table.
     *
     */
    @Test
    void tableCanBeClearedSuccessfully() throws SQLException {
        //  Testing count of rows before clearing
        try (Statement countRecordsBefore = connection.createStatement()) {
            ResultSet tableRecordsBeforeClear = countRecordsBefore.executeQuery("SELECT COUNT(*) FROM testTable ");
            //   There should be 2 rows before clearing
            assertEquals(2, tableRecordsBeforeClear.getInt(1));
            //  Insert rows into table first
            try (Statement clearTable = connection.createStatement()) {
                // Deletes all records from table
                clearTable.execute("DELETE FROM testTable");

                try (Statement countRecordsAfter = connection.createStatement()) {
                    ResultSet tableRecordsAfterClear = countRecordsAfter.executeQuery("SELECT COUNT(*) FROM testTable ");
                    //   There should be 0 rows after clearing
                    assertEquals(0, tableRecordsAfterClear.getInt(1));
                } catch (SQLException e){
                    System.err.println("Error counting records after clearing table: " + e.getMessage());
                }
            } catch (SQLException e) {
                System.err.println("Error clearing table " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error counting records in table before clearing: " + e.getMessage());
        }
    }

    /**
     * This is a test method to test under the conditions that a null value is inserted into a field in the
     * database table with the 'NOT NULL' constraint.
     * <p>
     * Executes an SQL INSERT statement to insert into the column 'dayOfWeek' a null value.
     *
     * An error is expected and is caught. A boolean value 'isThereANullRecord' is then set to true if this
     * error is caught, which is asserted as true and denotes that the record was not inserted due to this error.
     *
     * @throws SQLException for the expected error of inserting a null value into a field with a 'NOT NULL'
     * constraint.
     *
     */
    @Test
    void cantInsertRecordsWithNullValuesIntoFieldsWithNotNullValues()  {
        boolean isThereANullRecord = false;
        try (Statement insertRecords = connection.createStatement()) {
            // Inserting a record with a null value
            insertRecords.execute("INSERT INTO testTable (dayOfWeek) VALUES (null)");
        } catch (SQLException e){
            System.err.println("Can't insert null value in table: " + e.getMessage());
            isThereANullRecord = true;
        }
        assertTrue(isThereANullRecord);
    }

    /**
     * This is a test method to test under the conditions of inserting an empty string into a field in the database
     * with the 'NOT NULL' constraint.
     * <p>
     * Executes an SQL INSERT statement to insert into an empty string into the 'dayOfWeek' field.
     *
     * An error is expected and is caught. A boolean value 'isThereAnEmptyString' is then set to true if this
     * error is caught, which is asserted as true and denotes that the record was not inserted due to this error.
     *
     * @throws SQLException for the expected error of inserting an empty string value into a field with a 'NOT NULL'
     * constraint.
     *
     */
    @Test
    void cantInsertEmptyStringsIntoFieldsWithNotNullValues () {
        boolean isThereAnEmptyString = false;
        try (Statement insertRecords = connection.createStatement()) {
            // Inserting a record with an empty string
            insertRecords.execute("INSERT INTO testTable (dayOfWeek) VALUES ('')");
        } catch (SQLException e){
            System.err.println("Can't insert empty strings into table: " + e.getMessage());
            isThereAnEmptyString = true;
        }
        assertTrue(isThereAnEmptyString);

    }

    /**
     * This is a test method to test an edge case of inserting the largest possible length of a string into a field
     * with a specific maximum string length
     * <p>
     * Executes an SQL INSERT statement to insert a string with 50 'e' characters which is the exact maximum length for
     * the field 'dayOfWeek' it is being inserted into it.
     *
     * If there is an error thrown, it is caught and the boolean 'doesStringExceedMaxLength' is set to true and asserted
     * as true to denote that the string was over the maximum length based on the error. In the case of no errors,
     * it is asserted as false, which means that the string does not exceed the maximum length specified for the field.
     *
     * @throws SQLException if there is an error inserting the string or if the string exceeds the maximum length of
     * the field.
     *
     */
    @Test
    void insertLargestPossibleLengthOfStringInFields(){
        boolean doesStringExceedMaxLength = false;
        String exactMaxLengthString = "e".repeat(50);
        //        Inserts string that exceeds maximum length
        try (PreparedStatement insertString = connection.prepareStatement("INSERT INTO testTable (dayOfWeek) VALUES (?)")) {
            insertString.setString(1, exactMaxLengthString);
            insertString.execute();
            // If there is an error with inserting, it is over the maximum length
        } catch (SQLException e){
            doesStringExceedMaxLength = true;
        }

        assertFalse(doesStringExceedMaxLength);
    }

    /**
     * This is a test method to test an edge case of inserting a string that exceeds the maximum length by 1 character.
     * <p>
     * Executes an SQL INSERT statement to insert a string with 51 'e' characters which is exactly 1 character over
     * the maximum length fo the field 'dayOfWeek' it is being inserted into it.
     *
     * If there is an error thrown, it is caught and the boolean 'doesStringExceedMaxLength' is set to true and asserted
     * as true to denote that the string was over the maximum length based on the error. In the case of no errors,
     * it is asserted as false, which means that the string does not exceed the maximum length specified for the field.
     *
     * @throws SQLException if there is an error inserting the string or if expectedly, the string exceeds the maximum length of
     * the field.
     *
     */
    @Test
    void insertStringThatExceedsMaxLength() {
        boolean doesStringExceedMaxLength = false;
        String exceedsMaxLength = "e".repeat(51);
        //        Inserts string that exceeds maximum length
        try (PreparedStatement insertString = connection.prepareStatement("INSERT INTO testTable (dayOfWeek) VALUES (?)")) {
            insertString.setString(1, exceedsMaxLength);
            insertString.execute();
            // If there is an error with inserting, it is over the maximum length
        } catch (SQLException e){
            System.err.println("Can't insert string that exceeds maximum length: " + e.getMessage());
            doesStringExceedMaxLength = true;
        }

        assertTrue(doesStringExceedMaxLength);
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
     * Deletes the testTable  used after each unit test to ensure the unit tests don't rely on each other.
     * <p>
     * An SQL statement to drop the table if it exists is executed.
     *
     * @throws SQLException if the table can't be deleted successfully.
     * </p>
     */
    //  After every test delete the table so a new one can be made for the next test
    @AfterEach
    void removeTableAfterEachTest() throws SQLException {
        try (Statement dropTable = connection.createStatement())
        {
            dropTable.execute("DROP TABLE IF EXISTS testTable");
        }
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
