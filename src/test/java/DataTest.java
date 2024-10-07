import com.therealworld.fitschedule.model.FitScheduleDBConnection;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataTest {

    File testSchedulingDatabaseFile;
    private static Connection connection;
    String url = "jdbc:sqlite:testschedulingdatabase.db";

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

    @AfterEach
    void tearDown() throws SQLException {
        try (Statement dropTable = connection.createStatement()) {
            dropTable.execute("DROP TABLE IF EXISTS testTable");
        }
    }

    @Test
    void connectionToDatabaseShouldNotBeClosedOrNull() {
        try (Connection connection = FitScheduleDBConnection.getInstance()) {
            assertFalse(connection.isClosed());
            assertNotNull(connection);
        } catch (SQLException e) {
            System.err.println("Error connecting to database " + e.getMessage());
        }
    }

    @Test
    void databaseFileIsCreated() {
        // Verify that the database file was created
        assertNotNull(testSchedulingDatabaseFile);
        assertTrue(testSchedulingDatabaseFile.exists(), "Database file should exist.");
    }

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

    @AfterAll
    static void removeTestDatabase() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
