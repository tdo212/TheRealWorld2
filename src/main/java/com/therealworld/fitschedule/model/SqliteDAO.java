
package com.therealworld.fitschedule.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.DayOfWeek;


public class SqliteDAO {

    private Connection connection;

    public SqliteDAO() {
        this.connection = FitScheduleDBConnection.getInstance();  // Shared database connection
        try {
            System.out.println("Database URL: " + connection.getMetaData().getURL());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createTables();  // Create users, schedules, and goals tables
        addFitnessEventColumnIfNotExists();  // Ensure columns exists
        addGoalProgressColumnIfNotExists();
        createFitnessEventsTable();
    }

    // Create tables for users, schedules, and goals
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Create users table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "username VARCHAR(50) UNIQUE NOT NULL, " +
                            "password VARCHAR(60) NOT NULL, " +
                            "email VARCHAR(50) NOT NULL, " +
                            "phoneNumber VARCHAR(15) NOT NULL)"
            );
            System.out.println("Users table created or already exists.");

            // Create currentSchedule table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS currentSchedule (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "user_id INTEGER NOT NULL, " +
                            "dayOfWeek VARCHAR(50) NOT NULL, " +
                            "eventName VARCHAR(50), " +
                            "eventDescription VARCHAR(250), " +
                            "eventStartTime VARCHAR(50), " +
                            "eventEndTime VARCHAR(50), " +
                            "isFitnessEvent INTEGER DEFAULT 0, " +
                            "FOREIGN KEY(user_id) REFERENCES users(id))"
            );
            System.out.println("currentSchedule table created or already exists.");

            // Create goals table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS goals (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "user_id INTEGER NOT NULL, " +
                            "goal_type TEXT NOT NULL, " +
                            "goal_duration INTEGER NOT NULL, " +  // Store goal duration as INTEGER for weeks
                            "goal_period TEXT NOT NULL, " +
                            "goal_description TEXT, " +
                            "goal_completed INTEGER NOT NULL DEFAULT 0, " +  // Add this column to track if the goal is completed (0 = false, 1 = true)
                            "FOREIGN KEY(user_id) REFERENCES users(id))"
            );
            System.out.println("Goals table created or already exists.");
            // Create badges table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS badges (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "badge_name TEXT NOT NULL," +
                            "date_of_completion TEXT NOT NULL," +
                            "user_id INTEGER NOT NULL," +
                            "FOREIGN KEY(user_id) REFERENCES users(id))"
            );
            System.out.println("Badges table created or already exists.");
            // Create badges table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS total_goals_completed (" +
                            "user_id INTEGER PRIMARY KEY," +
                            "total_completed INTEGER NOT NULL DEFAULT 0," +
                            "FOREIGN KEY(user_id) REFERENCES users(id))"
            );
            System.out.println("Total_Goals table created or already exists.");
            // Create userProfile table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS userProfile (" +
                            "profile_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "user_id INTEGER NOT NULL, " +
                            "username TEXT NOT NULL, " +
                            "email INTEGER NOT NULL, " +  // Store goal duration as INTEGER for weeks
                            "training_frequency TEXT NULL, " +
                            "training_time TEXT, " +
                            "FOREIGN KEY(user_id) REFERENCES users(id))"
            );
            System.out.println("User Profile table created or already exists.");
        } catch (SQLException ex) {
            System.err.println("Error creating tables: " + ex.getMessage());
        }
    }
    // Call this method after creating the currentSchedule table
    private void addFitnessEventColumnIfNotExists() {
        String query = "ALTER TABLE currentSchedule ADD COLUMN isFitnessEvent INTEGER DEFAULT 0";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
            System.out.println("'isFitnessEvent' column added to currentSchedule.");
        } catch (SQLException ex) {
            if (ex.getMessage().contains("duplicate column name")) {
                System.out.println("'isFitnessEvent' column already exists.");
            } else {
                System.err.println("Error adding 'isFitnessEvent' column: " + ex.getMessage());
            }
        }
    }

    public void createWeeklyScheduleTable(String weekStartDate, int userId) {
        String tableName = "weeklySchedule_" + weekStartDate.replace("-", "_");  // Create a unique table name using the start date

        // Add backticks around the table name for proper escaping
        String createTableQuery = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "timeSlot TEXT NOT NULL, " +
                "isFitnessEvent INTEGER NOT NULL DEFAULT 0, " +  // Ensure this line is present
                "Monday TEXT, " +
                "Tuesday TEXT, " +
                "Wednesday TEXT, " +
                "Thursday TEXT, " +
                "Friday TEXT, " +
                "Saturday TEXT, " +
                "Sunday TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                "UNIQUE (user_id, timeSlot))";


        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableQuery);
            System.out.println("Weekly schedule table for week " + weekStartDate + " created or already exists.");
        } catch (SQLException ex) {
            System.err.println("Error creating new weekly schedule table: " + ex.getMessage());
        }
    }








    // Helper method to get user ID by username
    public int getUserId(String username) {
        String query = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if user is not found
    }

    // Helper method to get user ID by username
    public String getUsernameById(int userId) {
        String username = null;
        String query = "SELECT username FROM users WHERE id = ?"; // Adjust table/column names as necessary
        String url = "jdbc:sqlite:FitScheduleDBConnection.db"; // Make sure this path is correct
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                username = rs.getString("username"); // Assuming 'username' is the column name in your DB
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions as needed
        }
        return username;
    }

    // Populate the weekly schedule with time slots for a user
    public void populateTimeSlots(int userId, String currentWeekStartDate) {
        String[] timeSlots = {
                "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM",
                "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
                "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
        };

        String insertQuery = "INSERT OR IGNORE INTO `" + currentWeekStartDate + "` (timeSlot, user_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            List<Integer> userIds = getAllUserIds();  // Get all user IDs to populate for each user
            for (int currentUserId : userIds) {
                for (String timeSlot : timeSlots) {
                    pstmt.setString(1, timeSlot);
                    pstmt.setInt(2, userId);
                    pstmt.executeUpdate();
                }
            }
            System.out.println("Time slots populated in weekly schedule for all users.");
        } catch (SQLException ex) {
            System.err.println("Error populating time slots: " + ex.getMessage());
        }
    }
    public void insertWeeklyEvent(int userId, String timeSlot, String dayOfWeek,
                                  String eventDescription, String currentWeekStartDate,
                                  boolean isFitnessEvent) {
        // Generate the table name dynamically based on the week start date
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");

        // Validate the dayOfWeek
        List<String> validDays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        if (!validDays.contains(dayOfWeek)) {
            throw new IllegalArgumentException("Invalid day of the week: " + dayOfWeek);
        }

        // Ensure the table exists
        if (!doesTableExist(tableName)) {
            createWeeklyScheduleTable(currentWeekStartDate, userId);
        }

        // Use UPDATE for the specific day and timeslot
        String updateQuery = "UPDATE " + tableName + " SET " + dayOfWeek + " = ?, isFitnessEvent = ? " +
                "WHERE timeSlot = ? AND user_id = ?";

        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setString(1, eventDescription);
            updateStmt.setInt(2, isFitnessEvent ? 1 : 0);
            updateStmt.setString(3, timeSlot);
            updateStmt.setInt(4, userId);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                // If no row was updated, insert a new row
                String insertQuery = "INSERT INTO " + tableName +
                        " (timeSlot, user_id, " + dayOfWeek + ", isFitnessEvent) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, timeSlot);
                    insertStmt.setInt(2, userId);
                    insertStmt.setString(3, eventDescription);
                    insertStmt.setInt(4, isFitnessEvent ? 1 : 0);
                    insertStmt.executeUpdate();
                }
            }

            System.out.println("Event inserted/updated for " + dayOfWeek + " at " + timeSlot + " in table " + tableName);
        } catch (SQLException ex) {
            System.err.println("Error inserting/updating weekly event: " + ex.getMessage());
        }
    }






    // Retrieve the weekly schedule for a specific user from the dynamically named table
    public List<String[]> getWeeklyScheduleForWeek(int userId, String currentWeekStartDate) {
        List<String[]> schedule = new ArrayList<>();
        // Dynamically generate the table name based on the week start date
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");
        System.out.println("Querying table: " + tableName + " for user ID: " + userId);

        // Check if the table exists, if not, create it
        if (!doesTableExist(tableName)) {
            createWeeklyScheduleTable(tableName, userId);
        }

        // Updated query to retrieve from the dynamically named table without filtering by date
        String query = "SELECT timeSlot, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday " +
                "FROM `" + tableName + "` WHERE user_id = ?";


        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);  // Set the userId parameter

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] row = new String[8];
                row[0] = rs.getString("timeSlot");      // timeSlot column
                row[1] = rs.getString("Monday");        // Monday column
                row[2] = rs.getString("Tuesday");       // Tuesday column
                row[3] = rs.getString("Wednesday");     // Wednesday column
                row[4] = rs.getString("Thursday");      // Thursday column
                row[5] = rs.getString("Friday");        // Friday column
                row[6] = rs.getString("Saturday");      // Saturday column
                row[7] = rs.getString("Sunday");        // Sunday column
                schedule.add(row);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving weekly schedule for user " + userId + " from table " + tableName + ": " + ex.getMessage());
        }
        return schedule;
    }



    public boolean doesTableExist(String tableName) {
        String checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkTableQuery)) {
            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("table " + tableName + "does exist");
            return rs.next();  // Return true if a table with this name exists
        } catch (SQLException ex) {
            System.err.println("Error checking if table exists: " + ex.getMessage());
            return false;
        }
    }




    // Retrieve all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";  // Retrieve all details of the users

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                // Create a new User object and populate it with the user details
                User user = new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phoneNumber")
                );
                users.add(user);  // Add the user to the list
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving users: " + ex.getMessage());
        }
        return users;
    }

    // Retrieve all user IDs
    public List<Integer> getAllUserIds() {
        List<Integer> userIds = new ArrayList<>();
        String query = "SELECT id FROM users";  // Query to retrieve only user IDs

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                userIds.add(rs.getInt("id"));  // Add the user ID to the list
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving user IDs: " + ex.getMessage());
        }
        return userIds;
    }




    // Add a new user
    public void addUser(String username, String password, String email, String phoneNumber) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO users (username, password, email, phoneNumber) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            pstmt.setString(4, phoneNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertSchedule(int userId, String dayOfWeek, String eventName, String eventDescription,
                               String eventStartTime, String eventEndTime, boolean isFitnessEvent) {
        String query = "INSERT INTO currentSchedule (user_id, dayOfWeek, eventName, eventDescription, " +
                "eventStartTime, eventEndTime, isFitnessEvent) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, dayOfWeek);
            pstmt.setString(3, eventName);
            pstmt.setString(4, eventDescription);
            pstmt.setString(5, eventStartTime.trim());  // Trim spaces before inserting
            pstmt.setString(6, eventEndTime);
            pstmt.setInt(7, isFitnessEvent ? 1 : 0);  // Set 1 for true, 0 for false

            pstmt.executeUpdate();
            System.out.println("Event added successfully.");
        } catch (SQLException ex) {
            System.err.println("Error inserting schedule: " + ex.getMessage());
        }
    }


    // Retrieve schedules for a specific user and day
    public List<Schedule> getScheduleForUser(int userId) {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM currentSchedule WHERE user_id = ? AND dayOfWeek = ?";
        String currentDay = LocalDate.now().getDayOfWeek().toString(); // Get today's day

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, currentDay);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Schedule schedule = new Schedule(
                        rs.getInt("id"),
                        rs.getString("dayOfWeek"),
                        rs.getString("eventName"),
                        rs.getString("eventDescription"),
                        rs.getString("eventStartTime"), // Correct this to use eventStartTime
                        rs.getString("eventEndTime"),   // Add this for the end time
                        rs.getInt("isFitnessEvent") == 1 // Retrieve the isFitnessEvent value
                );

                schedules.add(schedule);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving schedules for user: " + ex.getMessage());
        }
        return schedules;
    }


    public List<Schedule> getCommitmentsForDay(int userId, String dayOfWeek) {
        List<Schedule> schedules = new ArrayList<>();

        // Generate the dynamic table name based on the current week start date
        String currentWeekStartDate = LocalDate.now().with(DayOfWeek.MONDAY).toString(); // Assuming Monday as the start of the week
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");

        // Normalize the dayOfWeek (convert "TUESDAY" to "Tuesday")
        dayOfWeek = capitalizeFirstLetter(dayOfWeek);

        // Ensure the dayOfWeek is valid (e.g., "Monday", "Tuesday", ...)
        if (!isValidDayOfWeek(dayOfWeek)) {
            System.err.println("Invalid dayOfWeek: " + dayOfWeek);
            return schedules;  // Return an empty list if the day is invalid
        }

        // Check if the table exists, if not, create it
        if (!doesTableExist(tableName)) {
            createWeeklyScheduleTable(tableName, userId);
        }

        // Query the dynamically generated table for the specific day
        String query = "SELECT id, timeSlot, `" + dayOfWeek + "` AS eventName, isFitnessEvent FROM `" + tableName + "` WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId); // Set the userId parameter

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Schedule schedule = new Schedule(
                        rs.getInt("id"),                   // Retrieve the event's id
                        dayOfWeek,                         // Use the dayOfWeek passed to the method
                        rs.getString("eventName"),          // Retrieves the event for the specific day
                        "",                                // Event description (if available elsewhere)
                        rs.getString("timeSlot"),           // Retrieve the timeSlot
                        rs.getString("timeSlot"),           // End time would be the next time block, if needed (optional)
                        rs.getInt("isFitnessEvent") == 1    // Convert integer to boolean
                );
                schedules.add(schedule);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving commitments for day: " + ex.getMessage());
        }
        return schedules;
    }


    // Helper method to normalize the dayOfWeek string (capitalize first letter)
    public String capitalizeFirstLetter(String dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek.isEmpty()) {
            return dayOfWeek;
        }
        return dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
    }

    // Helper method to validate the day of the week
    public boolean isValidDayOfWeek(String dayOfWeek) {
        String[] validDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        return Arrays.asList(validDays).contains(dayOfWeek);
    }



    // Delete a schedule by ID
    public void deleteSchedule(int scheduleId) {
        String query = "DELETE FROM currentSchedule WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, scheduleId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error deleting schedule: " + ex.getMessage());
        }
    }

    // Add a goal for a user
    public void addGoal(int userId, String goalType, int goalDuration, String goalPeriod, String goalDescription, int goalCompleted) {
        String query = "INSERT INTO goals (user_id, goal_type, goal_duration, goal_period, goal_description, goal_completed) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, goalType);
            pstmt.setInt(3, goalDuration);
            pstmt.setString(4, goalPeriod);
            pstmt.setString(5, goalDescription);
            pstmt.setInt(6, goalCompleted); // Add the goal_completed parameter

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Get all goals for a user


    // Delete a goal by ID
    public void deleteGoalFromDatabase(int goalId) {
        String query = "DELETE FROM goals WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update a schedule
    public void updateSchedule(Schedule schedule) {
        String query = "UPDATE currentSchedule SET dayOfWeek = ?, eventName = ?, eventDescription = ?, eventStartTime = ?, eventEndTime = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, schedule.getDayOfWeek());
            pstmt.setString(2, schedule.getEventName());
            pstmt.setString(3, schedule.getEventDescription());
            pstmt.setString(4, schedule.getEventStartTime());
            pstmt.setString(5, schedule.getEventEndTime());
            pstmt.setInt(6, schedule.getId());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error updating schedule: " + ex.getMessage());
        }
    }

    // Update a goal
    public void updateGoal(int goalId, String goalType, int goalDuration, String goalPeriod, String goalDescription) {
        String query = "UPDATE goals SET goal_type = ?, goal_duration = ?, goal_period = ?, goal_description = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, goalType);
            pstmt.setInt(2, goalDuration);
            pstmt.setString(3, goalPeriod);
            pstmt.setString(4, goalDescription);
            pstmt.setInt(5, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearScheduleForUser(int userId, String currentWeekStartDate) {
        // Generate the table name dynamically based on the week start date
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");

        // Check if the table exists to avoid errors
        if (!doesTableExist(tableName)) {
            System.out.println("Table " + tableName + " does not exist.");
            return;
        }

        // Delete all entries for the user in the specific weekly schedule table
        String query = "DELETE FROM " + tableName + " WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();

            System.out.println("Cleared " + rowsAffected + " schedule entries for user " + userId + " in table " + tableName);
        } catch (SQLException ex) {
            System.err.println("Error clearing schedule for user " + userId + " in table " + tableName + ": " + ex.getMessage());
        }
    }



    // Method to validate password
    public boolean validatePassword(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                // Validate the provided password with the hashed password from the database
                return BCrypt.checkpw(password, hashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Return false if user is not found or if there is an error
    }

    public boolean authenticateUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // Compare the provided password with the stored (hashed) password
                return BCrypt.checkpw(password, storedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Return false if user not found or error occurred
    }
    public static ObservableList<Goal> getAllGoals(int userId) {
        ObservableList<Goal> data = FXCollections.observableArrayList();
        String url = "jdbc:sqlite:FitScheduleDBConnection.db"; // Make sure this path is correct
        String query = "SELECT * FROM goals WHERE user_id = ? AND goal_completed = 0"; // Query to fetch goals for a specific user
        try (Connection conn = DriverManager.getConnection(url);

             PreparedStatement pstmt = conn.prepareStatement(query)) {  // Use PreparedStatement
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Construct a Goal object with all the columns in the goals table
                Goal goalEntry = new Goal(
                        rs.getInt("id"),              // goalId
                        rs.getString("goal_type"),     // goalType
                        rs.getInt("goal_duration"),    // goalDuration
                        rs.getString("goal_period"),   // goalPeriod
                        rs.getString("goal_description"), // goalDescription
                        rs.getBoolean("goal_completed"), // goalCompleted
                        rs.getInt("goal_progress")     // goalProgress (retrieved as an integer)
                );
                System.out.println("Fetched goal entry: " + goalEntry);
                data.add(goalEntry);  // Add the Goal object to the ObservableList
            }

            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Data loaded: " + data.size() + " items.");
        return data;
    }

    public int countGoals() {
        String sql = "SELECT COUNT(*) AS count FROM goals";
        int count = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return count;
    }
    public int countGoalsRemaining(int userId) {
        String sql = "SELECT COUNT(*) AS count FROM goals WHERE goal_completed = 0 AND user_id = ?";
        int count = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return count;
    }

    public void updateGoalProgress(int goalId, int newProgress) {
        String query = "UPDATE goals SET goal_progress = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, newProgress);  // Set the new progress
            pstmt.setInt(2, goalId);       // Specify the goal ID to update
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Goal progress updated successfully for goal ID: " + goalId + " modified by: " + newProgress);
            } else {
                System.out.println("No rows updated. Check if goal ID exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating goal progress: " + e.getMessage());
        }
    }



    public void updateGoalAsCompleted(int goalId) {
        String query = "UPDATE goals SET goal_completed = 1 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getCompletedGoalsCount(int userId) {
        String query = "SELECT COUNT(*) FROM goals WHERE goal_completed = 1 AND user_id = ?";
        int count = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:FitScheduleDBConnection.db");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            // Retrieve the count
            if (rs.next()) {
                count = rs.getInt(1); // Get the first column from the result set (the count)
            }

            rs.close(); // Close the ResultSet
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }
    public void awardBadge(int userId, String badgeName) {
        String query = "INSERT INTO badges (user_id, badge_name, date_of_completion) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, badgeName);
            pstmt.setString(3, LocalDate.now().toString());  // Use current date
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<String> getUserBadges(int userId) {
        ObservableList<String> badges = FXCollections.observableArrayList();
        String query = "SELECT badge_name, date_of_completion FROM badges WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String badge = String.format("%s - Earned on %s", rs.getString("badge_name"), rs.getString("date_of_completion"));
                badges.add(badge);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return badges;
    }
    public void initializeTotalGoalsCompleted(int userId) {
        String query = "INSERT INTO total_goals_completed (user_id, total_completed) VALUES (?, 0) ON CONFLICT(user_id) DO NOTHING";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getTotalGoalsCompleted(int userId) {
        String query = "SELECT total_completed FROM total_goals_completed WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_completed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;  // Default to 0 if no entry exists
    }

    public void incrementTotalGoalsCompleted(int userId) {
        String query = "UPDATE total_goals_completed SET total_completed = total_completed + 1 WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public UserProfile fetchProfileDetails(int userId) {
        String email = null;
        String username = null;
        String phoneNumber = null;
        UserProfile userProfile = null;
        String query = "SELECT username, email, phoneNumber FROM users WHERE users.id = ?";
        String url = "jdbc:sqlite:FitScheduleDBConnection.db";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
                email = rs.getString("email");
                phoneNumber = rs.getString("phoneNumber");


                userProfile = new UserProfile(username,email,phoneNumber); // Creates new user profile object
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userProfile;
    }

    // Add goal_progress column to the goals table if it does not already exist
    private void addGoalProgressColumnIfNotExists() {
        String query = "ALTER TABLE goals ADD COLUMN goal_progress INTEGER DEFAULT 0";  // Add the new column with a default value of 0
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
            System.out.println("'goal_progress' column added to goals table.");
        } catch (SQLException ex) {
            if (ex.getMessage().contains("duplicate column name")) {
                System.out.println("'goal_progress' column already exists.");
            } else {
                System.err.println("Error adding 'goal_progress' column: " + ex.getMessage());
            }
        }
    }

    // Table creation code
    public void createFitnessEventsTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS fitness_events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "event_name TEXT NOT NULL, " +
                "day_of_week TEXT NOT NULL, " +
                "time_slot TEXT NOT NULL, " +
                "week_start_date TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableQuery);
            System.out.println("Fitness events table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating fitness events table: " + e.getMessage());
        }
    }

    // Insert method for individual time slots
    public void insertIntoFitnessEvents(int userId, String eventName, String dayOfWeek, String timeSlot, String weekStartDate) {
        String insertQuery = "INSERT INTO fitness_events (user_id, event_name, day_of_week, time_slot, week_start_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, eventName);
            pstmt.setString(3, dayOfWeek);
            pstmt.setString(4, timeSlot);
            pstmt.setString(5, weekStartDate);
            pstmt.executeUpdate();
            System.out.println("Fitness event successfully added for " + dayOfWeek + " at times: " + timeSlot);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Schedule> getFitnessEventsForDay(int userId, String dayOfWeek) {
        List<Schedule> fitnessEvents = new ArrayList<>();
        String query = "SELECT * FROM fitness_events WHERE user_id = ? AND day_of_week = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, dayOfWeek);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String eventId = String.valueOf(rs.getInt("id"));  // Convert int to String for event ID
                String eventName = rs.getString("event_name");
                String startTime = rs.getString("time_slot");

                Schedule event = new Schedule(
                        eventId,                 // ID as a String
                        dayOfWeek,               // Day of the week
                        eventName,               // Event name
                        "",                      // Event description
                        startTime,               // Start time
                        true                     // isFitnessEvent is always true for this method
                );
                fitnessEvents.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving fitness events: " + e.getMessage());
        }
        return fitnessEvents;
    }

}
