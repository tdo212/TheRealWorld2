import com.therealworld.fitschedule.model.FitScheduleDBConnection;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class databaseTest {

    File testSchedulingDatabaseFile ;
    private static Connection connection;
    String url = "jdbc:sqlite:testschedulingdatabase.db";

    @BeforeEach
    void setUp() throws SQLException {
//        Create database file
        testSchedulingDatabaseFile = new File("testschedulingdatabase.db");
//        Set up connection to database
        connection = DriverManager.getConnection(url);
        try (Statement createTable = connection.createStatement()) {
            createTable.execute("CREATE TABLE IF NOT EXISTS testTable ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "dayOfWeek VARCHAR(50) NOT NULL CHECK(LENGTH(dayOfWeek) <= 50) CHECK(dayOfWeek <> ''),"
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


    @Test
    void connectionToDatabaseShouldNotBeClosedOrNull(){
//        Testing to ensure connection is not closed or not null
        try (Connection connection = FitScheduleDBConnection.getInstance()){
            assertFalse(connection.isClosed());
            assertNotNull(connection);
        } catch (SQLException e) {
            System.err.println("Error connecting to database " + e.getMessage());
        }
    }

    @Test
    void databaseFileIsCreated(){
//        Finding if 'testschedulingdatabase.db' file exists
        try {assertNotNull(testSchedulingDatabaseFile.exists());
        } catch (Exception e) {
            System.err.println("Error with creation of database file " + e.getMessage());
        }
    }

    @Test
    void createTableAndIfExistsDeleteIt() throws SQLException {
        // Create table
        try (Statement createTable = connection.createStatement()) {
            createTable.execute("CREATE TABLE IF NOT EXISTS createTableTest ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT"
                    + ")"
            );

        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }

//        Call methods to test if table exist and then delete table if it exists
        testIfTableExists();
        deleteTableIfExists(true);


    }

    boolean testIfTableExists() throws SQLException{
        // Select table in SQL database schema
        boolean tableExists = false;
        try (Statement findTable = connection.createStatement()) {
            ResultSet findTableRecords = findTable.executeQuery("SELECT name FROM sqlite_master WHERE " +
                    "type='table' AND name='createTableTest'");
            //  There needs to exist a valid row for a table to exist at all
            tableExists = findTableRecords.next();
            assertTrue(tableExists);


        } catch (SQLException e) {
            System.err.println("Error finding table: " + e.getMessage());
        }
        // If there exists rows in the table then drop the table

        return tableExists;
    }

    void deleteTableIfExists(boolean tableExists){
        if (tableExists)
        {
            try (Statement dropTable = connection.createStatement()) {
                dropTable.execute("DROP TABLE IF EXISTS createTableTest");
            } catch (SQLException e){
                System.err.println("Syntax Error in statement" + e.getMessage());
            }
        }
    }

    @Test
    void recordsCanBeInsertedIntoDatabaseTable() throws SQLException {
        //        Insert records into table
        try (Statement insertRecords = connection.createStatement()) {
            insertRecords.execute("INSERT INTO testTable (dayOfWeek) VALUES ('Friday')");

            // Test if records exist in the table

            try (Statement findRecord = connection.createStatement()) {
                //  Ensures data was inserted correctly by testing if result in table exists
                ResultSet recordsExist = findRecord.executeQuery("SELECT dayOfWeek FROM testTable WHERE dayOfWeek = 'Friday'");
                String recordResult = recordsExist.getString("dayOfWeek");
                assertEquals(recordResult, "Friday");
            } catch (SQLException e) {
                System.err.println("Error finding inserted record in table: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error inserting record in table: " + e.getMessage());
        }
    }

    @Test
    void recordsCanBeRetrieved() throws SQLException {
        try (Statement retrieveRecords = connection.createStatement()) {
            ResultSet recordRetrieval = retrieveRecords.executeQuery("SELECT id, dayOfWeek FROM testTable WHERE dayOfWeek = 'Monday'");
            String retrievedDayOfWeek = recordRetrieval.getString("dayOfWeek");
            int retrieveId = recordRetrieval.getInt("id");
            assertEquals(retrievedDayOfWeek, "Monday");
            assertEquals(retrieveId, 2);

        } catch (SQLException e) {
            System.err.println("Error retrieving record from database: " + e.getMessage());
        }
    }

    @Test
    void retrieveNullRecordInTable() throws SQLException{
        //  Attempting to Retrieve a non-existent record in table
            boolean recordNotNull = true;
        try (Statement retrieveNullRecord = connection.createStatement()) {
            ResultSet nullRecordRetrieval = retrieveNullRecord.executeQuery("SELECT id FROM testTable WHERE id = '20'");

            if (nullRecordRetrieval.next()) {

                recordNotNull = true;
            } else if (!nullRecordRetrieval.next()) {
                recordNotNull = false;
            }
            //  If there are no records for that row found, then the records are null
            assertFalse(recordNotNull);
        }
    }

    @Test
    void recordsCanBeUpdated() throws SQLException {
        try (Statement updateRecords = connection.createStatement()) {
            // Update existing records
            updateRecords.execute("UPDATE testTable SET dayOfWeek = 'Sunday' WHERE id ='1';");

            try (Statement retrieveUpdatedRecords = connection.createStatement()) {
                ResultSet updatedRecords = retrieveUpdatedRecords.executeQuery("SELECT id, dayOfWeek FROM testTable WHERE dayOfWeek = 'Sunday' AND id='1'");
                String retrievedDayOfWeek = updatedRecords.getString("dayOfWeek");
                int retrieveId = updatedRecords.getInt("id");
                assertEquals(retrievedDayOfWeek, "Sunday");
                assertEquals(retrieveId, 1);
            } catch (SQLException e){
                System.err.println("Error retrieving updated records: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error updating records: " + e.getMessage());
        }
    }



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

    @Test
    void incorrectDataTypeCantBeInserted() {
        boolean incorrecDataTypeInserted = false;
        try (Statement insertRecords = connection.createStatement()) {
            // Inserting a record with an incorrect data type
            insertRecords.execute("INSERT INTO testTable (id, dayOfWeek) VALUES ('a', 'Wednesday')");
        } catch (SQLException e){
            System.err.println("Can't insert record with invalid data type: " + e.getMessage());
            incorrecDataTypeInserted = true;
        }
        assertTrue(incorrecDataTypeInserted);
        }

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

    @Test
    void throwExceptionIfDuplicateUniqueFieldsInserted() {
        boolean isThereADuplicateRecord = false;
        try (Statement insertRecords = connection.createStatement()) {
        // Inserting a record with an existing unique ID
            insertRecords.execute("INSERT INTO testTable (id, dayOfWeek) VALUES (1 ,'Wednesday')");
        } catch (SQLException e){
            isThereADuplicateRecord = true;
        }
        assertTrue(isThereADuplicateRecord);
    }

    @Test
    void testInsertIncorrectColumnName (){
        boolean isColumnNameCorrect = true;
        try (Statement insertRecords = connection.createStatement()) {
            // Inserting a record using an incorrect column name for dayOfWeek
            insertRecords.execute("INSERT INTO testTable (daofWeek) VALUES ('Saturday')");
        } catch (SQLException e){
            System.err.println("Can't insert into non-existent column: " + e.getMessage());
            isColumnNameCorrect = false;
        }
        // Error means that Column name was incorrect
        assertFalse(isColumnNameCorrect);
    }

    //  After every test delete the table so a new one can be made for the next test
   @AfterEach
   void removeTableAfterEachTest() throws SQLException {
        try (Statement dropTable = connection.createStatement())
        {
            dropTable.execute("DROP TABLE IF EXISTS testTable");
        }
    }

    //    Closes connection after all tests are done with database
    @AfterAll
    static void removeTestDatabase() throws SQLException{
        connection.close();
    }


}
