
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

    /**
     * Establishes a connection to the SQLite database and initializes tables required for
     * the application, ensuring any necessary columns are added if they do not exist.
     */
    public SqliteDAO() {
        this.connection = FitScheduleDBConnection.getInstance();  // Shared database connection
        try {
            System.out.println("Database URL: " + connection.getMetaData().getURL());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createTables();  // Create users, schedules, and goals tables
        addFitnessEventColumnIfNotExists();  // Ensure columns exist
        addGoalProgressColumnIfNotExists();
        createFitnessEventsTable();
    }

    /**
     * Creates necessary tables in the database for users, schedules, goals, badges,
     * total goals completed, and user profiles. This method is called during initialization
     * to ensure the tables are set up if they do not already exist.
     */
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
                            "goal_completed INTEGER NOT NULL DEFAULT 0, " +  // Track if the goal is completed (0 = false, 1 = true)
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

            // Create total_goals_completed table
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

    /**
     * Adds the 'isFitnessEvent' column to the currentSchedule table if it does not already exist.
     * This column is used to mark events as fitness-related activities.
     */
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


    /**
     * Creates a weekly schedule table for a user based on the specified week's start date.
     * The table is named using the start date to ensure unique weekly tables.
     *
     * @param weekStartDate The start date of the week in "yyyy-MM-dd" format.
     * @param userId The unique ID of the user.
     */
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

    /**
     * Retrieves the unique user ID associated with a specified username.
     *
     * @param username The username to search for.
     * @return The user ID if found, or -1 if no matching user is found.
     */
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

    /**
     * Retrieves the username associated with a given user ID.
     *
     * @param userId The unique ID of the user.
     * @return The username if found, or null if no matching user is found.
     */
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

    /**
     * Populates the weekly schedule table with predefined time slots for a given user.
     *
     * @param userId The unique ID of the user.
     * @param currentWeekStartDate The start date of the current week.
     */
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

    /**
     * Inserts or updates a weekly event for a specific user, day, and time slot in the weekly schedule table.
     * The method dynamically creates a table name based on the week's start date.
     *
     * @param userId The unique ID of the user.
     * @param timeSlot The time slot for the event.
     * @param dayOfWeek The day of the week for the event (e.g., "Monday").
     * @param eventDescription Description of the event.
     * @param currentWeekStartDate The start date of the current week.
     * @param isFitnessEvent True if the event is fitness-related, false otherwise.
     * @throws IllegalArgumentException if an invalid day of the week is provided.
     */
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







    /**
     * Retrieves the weekly schedule for a specific user from a dynamically named table
     * based on the provided week start date.
     *
     * @param userId The unique ID of the user.
     * @param currentWeekStartDate The start date of the current week in "yyyy-MM-dd" format.
     * @return A list of string arrays representing the weekly schedule, with each array
     *         containing the time slot and events for each day of the week.
     */
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

    /**
     * Checks if a table with the specified name exists in the SQLite database.
     *
     * @param tableName The name of the table to check.
     * @return True if the table exists, otherwise false.
     */
    public boolean doesTableExist(String tableName) {
        String checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkTableQuery)) {
            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Table " + tableName + " does exist.");
            return rs.next();  // Return true if a table with this name exists
        } catch (SQLException ex) {
            System.err.println("Error checking if table exists: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all user information from the database.
     *
     * @return A list of User objects, each representing a user with details like
     *         username, password, email, and phone number.
     */
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

    /**
     * Retrieves all user IDs from the database.
     *
     * @return A list of integers, each representing a unique user ID.
     */
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





    /**
     * Adds a new user to the database with a hashed password.
     *
     * @param username The username of the new user.
     * @param password The password of the new user (will be hashed before storage).
     * @param email The email address of the new user.
     * @param phoneNumber The phone number of the new user.
     */
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

    /**
     * Inserts a new event into the user's schedule with details including the event name,
     * description, start and end times, and fitness event status.
     *
     * @param userId The unique ID of the user.
     * @param dayOfWeek The day of the week on which the event takes place.
     * @param eventName The name of the event.
     * @param eventDescription A description of the event.
     * @param eventStartTime The start time of the event.
     * @param eventEndTime The end time of the event.
     * @param isFitnessEvent True if the event is a fitness-related event, false otherwise.
     */
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

    /**
     * Retrieves the schedules for a specific user for today. Today's day is determined using
     * the local system's day of the week.
     *
     * @param userId The unique ID of the user.
     * @return A list of {@link Schedule} objects representing the user's schedule for today.
     */
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
                        rs.getString("eventStartTime"),
                        rs.getString("eventEndTime"),
                        rs.getInt("isFitnessEvent") == 1
                );

                schedules.add(schedule);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving schedules for user: " + ex.getMessage());
        }
        return schedules;
    }

    /**
     * Retrieves the commitments for a specific user and day from the dynamically generated
     * weekly schedule table. Creates the table if it does not exist.
     *
     * @param userId The unique ID of the user.
     * @param dayOfWeek The day of the week for which to retrieve commitments.
     * @return A list of {@link Schedule} objects representing the commitments for the specified day.
     */
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
                        rs.getInt("id"),
                        dayOfWeek,
                        rs.getString("eventName"),
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



    /**
     * Normalizes the day of the week by capitalizing the first letter and converting
     * the rest to lowercase.
     *
     * @param dayOfWeek The day of the week as a string.
     * @return The formatted day of the week with the first letter capitalized.
     */
    public String capitalizeFirstLetter(String dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek.isEmpty()) {
            return dayOfWeek;
        }
        return dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
    }

    /**
     * Validates if the given day of the week is valid (i.e., one of Monday, Tuesday, etc.).
     *
     * @param dayOfWeek The day of the week as a string.
     * @return True if the dayOfWeek is valid, false otherwise.
     */
    public boolean isValidDayOfWeek(String dayOfWeek) {
        String[] validDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        return Arrays.asList(validDays).contains(dayOfWeek);
    }

    /**
     * Deletes a specific schedule entry from the database using its unique ID.
     *
     * @param scheduleId The unique ID of the schedule to delete.
     */
    public void deleteSchedule(int scheduleId) {
        String query = "DELETE FROM currentSchedule WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, scheduleId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error deleting schedule: " + ex.getMessage());
        }
    }

    /**
     * Adds a new goal for a specific user in the database, including goal type, duration,
     * period, description, and completion status.
     *
     * @param userId The unique ID of the user.
     * @param goalType The type of goal.
     * @param goalDuration The duration of the goal.
     * @param goalPeriod The period over which the goal is measured (e.g., per week).
     * @param goalDescription A description of the goal.
     * @param goalCompleted Completion status of the goal (0 for not completed, 1 for completed).
     */
    public void addGoal(int userId, String goalType, int goalDuration, String goalPeriod, String goalDescription, int goalCompleted) {
        String query = "INSERT INTO goals (user_id, goal_type, goal_duration, goal_period, goal_description, goal_completed) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, goalType);
            pstmt.setInt(3, goalDuration);
            pstmt.setString(4, goalPeriod);
            pstmt.setString(5, goalDescription);
            pstmt.setInt(6, goalCompleted);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Deletes a specific goal from the database by its unique ID.
     *
     * @param goalId The unique ID of the goal to delete.
     */
    public void deleteGoalFromDatabase(int goalId) {
        String query = "DELETE FROM goals WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a schedule entry in the database with the provided schedule details.
     *
     * @param schedule The schedule object containing updated details to be saved.
     */
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

    /**
     * Updates an existing goal in the database with new goal details.
     *
     * @param goalId          The unique ID of the goal to update.
     * @param goalType        The type of the goal.
     * @param goalDuration    The duration of the goal.
     * @param goalPeriod      The period over which the goal is measured.
     * @param goalDescription The description of the goal.
     */
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

    /**
     * Clears the schedule entries for a specific user within the weekly schedule table.
     *
     * @param userId              The unique ID of the user whose schedule is being cleared.
     * @param currentWeekStartDate The start date of the week to identify the schedule table.
     */
    public void clearScheduleForUser(int userId, String currentWeekStartDate) {
        String tableName = "weeklySchedule_" + currentWeekStartDate.replace("-", "_");

        if (!doesTableExist(tableName)) {
            System.out.println("Table " + tableName + " does not exist.");
            return;
        }

        String query = "DELETE FROM " + tableName + " WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();

            System.out.println("Cleared " + rowsAffected + " schedule entries for user " + userId + " in table " + tableName);
        } catch (SQLException ex) {
            System.err.println("Error clearing schedule for user " + userId + " in table " + tableName + ": " + ex.getMessage());
        }
    }

    /**
     * Validates the provided password for a specific username by comparing it with the stored hashed password.
     *
     * @param username The username for which to validate the password.
     * @param password The password to validate.
     * @return True if the password is correct, false otherwise.
     */
    public boolean validatePassword(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return BCrypt.checkpw(password, hashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Authenticates a user by verifying the provided password against the stored hashed password.
     *
     * @param username The username of the user attempting to authenticate.
     * @param password The password to verify.
     * @return True if the user is authenticated successfully, false otherwise.
     */
    public boolean authenticateUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return BCrypt.checkpw(password, storedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all goals for a specific user from the database.
     *
     * @param userId The unique ID of the user whose goals are being retrieved.
     * @return An ObservableList of Goal objects representing the user's goals.
     */
    public static ObservableList<Goal> getAllGoals(int userId) {
        ObservableList<Goal> data = FXCollections.observableArrayList();
        String url = "jdbc:sqlite:FitScheduleDBConnection.db";
        String query = "SELECT * FROM goals WHERE user_id = ? AND goal_completed = 0";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Goal goalEntry = new Goal(
                        rs.getInt("id"),              // goalId
                        rs.getString("goal_type"),     // goalType
                        rs.getInt("goal_duration"),    // goalDuration
                        rs.getString("goal_period"),   // goalPeriod
                        rs.getString("goal_description"), // goalDescription
                        rs.getBoolean("goal_completed"), // goalCompleted
                        rs.getInt("goal_progress")     // goalProgress
                );
                System.out.println("Fetched goal entry: " + goalEntry);
                data.add(goalEntry);
            }

            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Data loaded: " + data.size() + " items.");
        return data;
    }


    /**
     * Counts the total number of goals in the database.
     *
     * @return The total count of goals.
     */
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

    /**
     * Counts the remaining (incomplete) goals for a specific user.
     *
     * @param userId The ID of the user whose remaining goals are being counted.
     * @return The count of incomplete goals for the specified user.
     */
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

    /**
     * Updates the progress of a specific goal.
     *
     * @param goalId The unique ID of the goal being updated.
     * @param newProgress The new progress value to set for the goal.
     */
    public void updateGoalProgress(int goalId, int newProgress) {
        String query = "UPDATE goals SET goal_progress = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, newProgress);
            pstmt.setInt(2, goalId);
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

    /**
     * Marks a goal as completed by setting its completed status to true (1).
     *
     * @param goalId The unique ID of the goal to mark as completed.
     */
    public void updateGoalAsCompleted(int goalId) {
        String query = "UPDATE goals SET goal_completed = 1 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the count of completed goals for a specific user.
     *
     * @param userId The ID of the user whose completed goals are being counted.
     * @return The count of completed goals for the specified user.
     */
    public int getCompletedGoalsCount(int userId) {
        String query = "SELECT COUNT(*) FROM goals WHERE goal_completed = 1 AND user_id = ?";
        int count = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:FitScheduleDBConnection.db");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }


    /**
     * Awards a badge to a user by inserting it into the badges table.
     *
     * @param userId    The ID of the user receiving the badge.
     * @param badgeName The name of the badge being awarded.
     */
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

    /**
     * Retrieves all badges earned by a user, along with their completion dates.
     *
     * @param userId The ID of the user whose badges are being retrieved.
     * @return A list of badges with their names and dates of completion.
     */
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

    /**
     * Initializes the total goals completed entry for a user if it does not already exist.
     *
     * @param userId The ID of the user whose goal completion total is being initialized.
     */
    public void initializeTotalGoalsCompleted(int userId) {
        String query = "INSERT INTO total_goals_completed (user_id, total_completed) VALUES (?, 0) ON CONFLICT(user_id) DO NOTHING";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the total count of goals completed by a user.
     *
     * @param userId The ID of the user whose completed goals count is being retrieved.
     * @return The total number of goals completed by the user.
     */
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

    /**
     * Increments the total goals completed count for a user by 1.
     *
     * @param userId The ID of the user whose completed goals count is being incremented.
     */
    public void incrementTotalGoalsCompleted(int userId) {
        String query = "UPDATE total_goals_completed SET total_completed = total_completed + 1 WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetches the profile details (username, email, and phone number) for a specific user.
     *
     * @param userId The ID of the user whose profile details are being retrieved.
     * @return A UserProfile object containing the user's details or null if the user is not found.
     */
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

                userProfile = new UserProfile(username, email, phoneNumber); // Creates new user profile object
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userProfile;
    }


    /**
     * Adds a 'goal_progress' column to the goals table if it does not already exist.
     * This column will store the progress of each goal, with a default value of 0.
     */
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

    /**
     * Creates the fitness_events table if it does not already exist. This table stores fitness events
     * with details such as user ID, event name, day of the week, time slot, and the start date of the week.
     */
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

    /**
     * Inserts an individual fitness event into the fitness_events table for a specified time slot.
     *
     * @param userId        The ID of the user who scheduled the fitness event.
     * @param eventName     The name of the fitness event.
     * @param dayOfWeek     The day of the week on which the event occurs.
     * @param timeSlot      The time slot during which the event is scheduled.
     * @param weekStartDate The start date of the week, used to organize events by week.
     */
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

    /**
     * Retrieves all fitness events for a specific user on a given day of the week.
     *
     * @param userId    The ID of the user whose fitness events are being retrieved.
     * @param dayOfWeek The day of the week for which to retrieve fitness events.
     * @return A list of Schedule objects representing the user's fitness events on the specified day.
     */
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
