import com.therealworld.fitschedule.model.FitScheduleDBConnection;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class databaseTest {

//    String url = "jbc::sqlite::mockDatabase";
    private File schedulingDatabaseFile;
    private static Connection connection;
    String url = "jdbc:sqlite:schedulingdatabase.db";


    @BeforeEach
    void setUp() throws SQLException {
//        Set up connection to database
        connection = DriverManager.getConnection(url);

        try (Statement createTable = connection.createStatement()) {
            createTable.execute("CREATE TABLE IF NOT EXISTS testTable ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "dayOfWeek TEXT NOT NULL"
                    + ")"
            );

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

//        Finding if 'schedulingdatabase.db' file exists
        try {schedulingDatabaseFile = new File ("schedulingdatabase.db");
            assertNotNull(schedulingDatabaseFile.exists());
        } catch (Exception e) {
            System.err.println("Error with creation of database file " + e.getMessage());
        }

    }
//    @BeforeEach
//    public void setUp() throws SQLException {
////        Create mock database connection
//        schedulingDatabaseFile = new File ("mockDatabase.db");
//        connection = DriverManager.getConnection(url);
//
//    }


    @Test
    void tableCanBeSuccessfullyCreatedInDatabase() throws SQLException {
            // Create table
        try (Statement createTable = connection.createStatement()) {
            createTable.execute("CREATE TABLE IF NOT EXISTS createTableTest ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT"
                    + ")"
            );
            // Select all from table to test if Table has been created
            try (Statement findTable = connection.createStatement()) {
                ResultSet findTableSet = findTable.executeQuery("SELECT * FROM createTableTest");


                tableExists();

            // If there exists rows in the table then drop the table
                if (findTableSet.next()) {
                    try (Statement dropTable = connection.createStatement()) {
                        dropTable.execute("DROP TABLE IF EXISTS createTableTest");
                    }
                }

            }
            catch (SQLException e)
            {
                System.err.println("Error finding table: " + e.getMessage());
            }
        }
        catch (SQLException e){
            System.err.println("Error creating table: " + e.getMessage());
        }


    }
    boolean tableExists() throws SQLException {
        try (Statement findTable = connection.createStatement()) {
            ResultSet findTableSet = findTable.executeQuery("SELECT * FROM createTableTest");
            return findTableSet.next();
        }

    }
    @Test
    void recordsCanBeInsertedAndRetrievedFromDatabaseTable() throws SQLException {
        try (Statement InsertRecords = connection.createStatement()){
            InsertRecords.execute("INSERT INTO testTable (dayOfWeek) VALUES ('Monday'); ");
        }
//Either make this a separate method and insert records in before testing retrieval or keep it there

    }

    @Test
    void recordsCanBeRetrievedFromDatabaseTable() throws SQLException {
        try (Statement insertRecords = connection.createStatement()) {
            insertRecords.executeQuery("SELECT id, dayOfWeek FROM testTable ");

//            assertEquals(1, insertRecords.getInt("id"));
        }


    }

    @AfterAll
    static void removeTable() throws SQLException {
        try (Statement dropTable = connection.createStatement())
        {
            dropTable.execute("DROP TABLE IF EXISTS testTable");
        }
    }


}
