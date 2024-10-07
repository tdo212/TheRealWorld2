package com.therealworld.fitschedule.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class responsible for interacting with the SQLite database for the FitSchedule application.
 * Provides methods to perform CRUD operations on users, schedules, goals, badges, and other data entities.
 */
public class SqliteDAO {

    /**
     * Connection instance for the SQLite database.
     * Used for executing SQL queries and managing transactions.
     */
    private Connection connection;

    /**
     * Default constructor that initializes the DAO with a shared database connection.
     * Creates necessary tables if they do not exist.
     */
    public SqliteDAO() {
        this.connection = FitScheduleDBConnection.getInstance();  // Shared database connection
        try {
            System.out.println("Database URL: " + connection.getMetaData().getURL());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createTables();  // Create users, schedules, and goals tables
    }

    /**
     * Creates tables for users, schedules, goals, badges, and total goals completed.
     * This method will only create tables if they do not already exist.
     */
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(60) NOT NULL, " +
                    "email VARCHAR(50) NOT NULL, " +
                    "phoneNumber VARCHAR(15) NOT NULL)");
            System.out.println("Users table created or already exists.");

            // Create currentSchedule table
            stmt.execute("CREATE TABLE IF NOT EXISTS currentSchedule (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "dayOfWeek VARCHAR(50) NOT NULL, " +
                    "eventName VARCHAR(50), " +
                    "eventDescription VARCHAR(250), " +
                    "eventStartTime VARCHAR(50), " +
                    "eventEndTime VARCHAR(50), " +
                    "FOREIGN KEY(user_id) REFERENCES users(id))");
            System.out.println("currentSchedule table created or already exists.");

            // Create goals table
            stmt.execute("CREATE TABLE IF NOT EXISTS goals (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "goal_type TEXT NOT NULL, " +
                    "goal_duration INTEGER NOT NULL, " +  // Store goal duration as INTEGER for weeks
                    "goal_period TEXT NOT NULL, " +
                    "goal_description TEXT, " +
                    "goal_completed INTEGER NOT NULL DEFAULT 0, " +  // Track if the goal is completed (0 = false, 1 = true)
                    "FOREIGN KEY(user_id) REFERENCES users(id))");
            System.out.println("Goals table created or already exists.");

            // Create badges table
            stmt.execute("CREATE TABLE IF NOT EXISTS badges (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "badge_name TEXT NOT NULL, " +
                    "date_of_completion TEXT NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id))");
            System.out.println("Badges table created or already exists.");

            // Create total_goals_completed table
            stmt.execute("CREATE TABLE IF NOT EXISTS total_goals_completed (" +
                    "user_id INTEGER PRIMARY KEY, " +
                    "total_completed INTEGER NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id))");
            System.out.println("Total_Goals table created or already exists.");
        } catch (SQLException ex) {
            System.err.println("Error creating tables: " + ex.getMessage());
        }
    }

    /**
     * Creates the weekly schedule table for a specified user.
     * This table is used to store the weekly events for a user.
     *
     * @param userId the ID of the user for whom the weekly schedule table should be created.
     */
    public void createWeeklyScheduleTable(int userId) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS weeklySchedule (" +
                    "timeSlot VARCHAR(10) NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "Monday TEXT, " +
                    "Tuesday TEXT, " +
                    "Wednesday TEXT, " +
                    "Thursday TEXT, " +
                    "Friday TEXT, " +
                    "Saturday TEXT, " +
                    "Sunday TEXT, " +
                    "PRIMARY KEY (timeSlot, user_id), " +  // Composite primary key: timeSlot + user_id
                    "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)");
            System.out.println("Weekly schedule table created or already exists.");
        } catch (SQLException ex) {
            System.err.println("Error creating weekly schedule table: " + ex.getMessage());
        }
    }


    /**
     * Retrieves the user ID for a given username.
     *
     * @param username the username to search for.
     * @return the user ID if found, otherwise -1.
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
     * Populates the weekly schedule table with predefined time slots for a specific user.
     *
     * @param userId the ID of the user for whom the time slots should be populated.
     */
    public void populateTimeSlots(int userId) {
        String[] timeSlots = {
                "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM",
                "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
                "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
        };

        String insertQuery = "INSERT OR IGNORE INTO weeklySchedule (timeSlot, user_id) VALUES (?, ?)";
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
     * Inserts a weekly event for a specified user.
     *
     * @param userId           the ID of the user to insert the event for.
     * @param timeSlot         the time slot of the event.
     * @param dayOfWeek        the day of the week for the event.
     * @param eventDescription a description of the event.
     */
    public void insertWeeklyEvent(int userId, String timeSlot, String dayOfWeek, String eventDescription) {
        String query = "UPDATE weeklySchedule SET " + dayOfWeek + " = ? WHERE timeSlot = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, eventDescription);
            pstmt.setString(2, timeSlot);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error inserting weekly event: " + ex.getMessage());
        }
    }

    /**
     * Retrieves the full weekly schedule for a specific user.
     *
     * @param userId the ID of the user to retrieve the schedule for.
     * @return a list of String arrays where each array represents a row in the schedule table
     * with time slots and events for each day of the week.
     */
    public List<String[]> getWeeklySchedule(int userId) {
        List<String[]> schedule = new ArrayList<>();
        String query = "SELECT * FROM weeklySchedule WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] row = new String[8];
                row[0] = rs.getString("timeSlot");
                row[1] = rs.getString("Monday");
                row[2] = rs.getString("Tuesday");
                row[3] = rs.getString("Wednesday");
                row[4] = rs.getString("Thursday");
                row[5] = rs.getString("Friday");
                row[6] = rs.getString("Saturday");
                row[7] = rs.getString("Sunday");
                schedule.add(row);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving weekly schedule for user " + userId + ": " + ex.getMessage());
        }
        return schedule;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of User objects containing details of each user.
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
     * @return a list of integers representing user IDs.
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
     * Adds a new user to the database.
     *
     * @param username    the username of the new user.
     * @param password    the plaintext password of the new user.
     * @param email       the email address of the new user.
     * @param phoneNumber the phone number of the new user.
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
     * Inserts a schedule into the `currentSchedule` table for a specific user.
     *
     * @param userId           the ID of the user for whom the schedule should be added.
     * @param dayOfWeek        the day of the week (e.g., "Monday") for the schedule.
     * @param eventName        the name of the event.
     * @param eventDescription a brief description of the event.
     * @param eventStartTime   the start time of the event.
     * @param eventEndTime     the end time of the event.
     */
    public void insertSchedule(int userId, String dayOfWeek, String eventName, String eventDescription, String eventStartTime, String eventEndTime) {
        String query = "INSERT INTO currentSchedule (user_id, dayOfWeek, eventName, eventDescription, eventStartTime, eventEndTime) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, dayOfWeek);
            pstmt.setString(3, eventName);
            pstmt.setString(4, eventDescription);
            pstmt.setString(5, eventStartTime);
            pstmt.setString(6, eventEndTime);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error inserting schedule: " + ex.getMessage());
        }
    }

    /**
     * Retrieves the schedules for a specific user and day of the week.
     *
     * @param userId the ID of the user to retrieve schedules for.
     * @return a list of `Schedule` objects representing the user's schedule for the current day.
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
                        rs.getString("timeSlot"),  // This is the time block
                        ""  // No end time
                );
                schedules.add(schedule);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving schedules for user: " + ex.getMessage());
        }
        return schedules;
    }

    /**
     * Retrieves the commitments for a specific user and day of the week.
     *
     * @param userId    the ID of the user to retrieve commitments for.
     * @param dayOfWeek the day of the week to retrieve commitments for (e.g., "Monday").
     * @return a list of `Schedule` objects representing the user's commitments for the specified day.
     */
    public List<Schedule> getCommitmentsForDay(int userId, String dayOfWeek) {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM currentSchedule WHERE user_id = ? AND dayOfWeek = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, dayOfWeek);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Schedule schedule = new Schedule(
                        rs.getInt("id"),
                        rs.getString("dayOfWeek"),
                        rs.getString("eventName"),
                        rs.getString("eventDescription"),
                        rs.getString("eventStartTime"),
                        rs.getString("eventEndTime")
                );
                schedules.add(schedule);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving commitments for day: " + ex.getMessage());
        }
        return schedules;
    }

    /**
     * Deletes a schedule entry from the `currentSchedule` table based on its ID.
     *
     * @param scheduleId the ID of the schedule entry to be deleted.
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
     * Adds a goal for a specific user in the `goals` table.
     *
     * @param userId          the ID of the user to whom the goal belongs.
     * @param goalType        the type of goal (e.g., "Fitness", "Academic").
     * @param goalDuration    the duration of the goal (e.g., number of weeks).
     * @param goalPeriod      the period of the goal (e.g., "Weekly").
     * @param goalDescription a brief description of the goal.
     * @param goalCompleted   the completion status of the goal (0 for incomplete, 1 for complete).
     */
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

    /**
     * Deletes a goal entry from the `goals` table based on its ID.
     *
     * @param goalId the ID of the goal entry to be deleted.
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
     * Updates a schedule entry in the `currentSchedule` table.
     *
     * @param schedule the `Schedule` object containing updated values for the schedule.
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
     * Updates a goal entry in the `goals` table based on its ID.
     *
     * @param goalId          the ID of the goal entry to be updated.
     * @param goalType        the type of the goal (e.g., "Fitness", "Academic").
     * @param goalDuration    the duration of the goal (e.g., number of weeks).
     * @param goalPeriod      the period of the goal (e.g., "Weekly").
     * @param goalDescription a brief description of the goal.
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
     * Clears all schedule entries for a specific user from the `currentSchedule` table.
     *
     * @param userId the ID of the user whose schedule should be cleared.
     */
    public void clearScheduleForUser(int userId) {
        String query = "DELETE FROM currentSchedule WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("Schedule cleared for user " + userId);
        } catch (SQLException ex) {
            System.err.println("Error clearing schedule: " + ex.getMessage());
        }
    }

    /**
     * Validates the provided password against the stored hashed password in the `users` table.
     *
     * @param username the username of the user.
     * @param password the plaintext password provided by the user.
     * @return `true` if the password matches the stored hash; `false` otherwise.
     */
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

    /**
     * Authenticates a user based on the provided username and password.
     *
     * @param username the username provided by the user.
     * @param password the plaintext password provided by the user.
     * @return `true` if the user is authenticated successfully; otherwise, `false`.
     */
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

    /**
     * Retrieves all goals stored in the `goals` table.
     *
     * @return an `ObservableList` of `Goal` objects representing all goals in the database.
     */
    public static ObservableList<Goal> getAllGoals() {
        ObservableList<Goal> data = FXCollections.observableArrayList();
        String url = "jdbc:sqlite:FitScheduleDBConnection.db"; // Make sure this path is correct

        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("Database connection established.");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM goals");

            while (rs.next()) {
                // Construct a Goal object with all the columns in the goals table
                Goal goalEntry = new Goal(
                        rs.getInt("id"),              // goalId
                        rs.getString("goal_type"),     // goalType
                        rs.getInt("goal_duration"),    // goalDuration
                        rs.getString("goal_period"),   // goalPeriod
                        rs.getString("goal_description"), // goalDescription
                        rs.getBoolean("goal_completed")  // goalCompleted
                );
                System.out.println("Fetched goal entry: " + goalEntry);
                data.add(goalEntry);  // Add the Goal object to the ObservableList
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Data loaded: " + data.size() + " items.");
        return data;
    }

    /**
     * Counts the total number of goals stored in the `goals` table.
     *
     * @return the total count of goals.
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
     * Marks a goal as completed by updating the `goal_completed` field to 1.
     *
     * @param goalId the ID of the goal to mark as completed.
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
     * Retrieves the count of goals marked as completed in the `goals` table.
     *
     * @return the number of completed goals.
     */
    public int getCompletedGoalsCount() {
        String query = "SELECT COUNT(*) FROM goals WHERE goal_completed = 1";
        int count = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:FitScheduleDBConnection.db");
             PreparedStatement pstmt = conn.prepareStatement(query)) {

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

    /**
     * Awards a badge to a user by inserting a new entry into the `badges` table.
     *
     * @param userId    the ID of the user who earned the badge.
     * @param badgeName the name of the badge.
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
     * Retrieves all badges earned by a user from the `badges` table.
     *
     * @param userId the ID of the user to retrieve badges for.
     * @return an `ObservableList` of badges (as strings) earned by the user.
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
     * Initializes the `total_goals_completed` table for a user with a default value of 0.
     *
     * @param userId the ID of the user for whom to initialize the total goals completed.
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
     * Retrieves the total number of goals completed by a user from the `total_goals_completed` table.
     *
     * @param userId the ID of the user.
     * @return the total number of goals completed by the user.
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
     * Increments the total number of goals completed by a user in the `total_goals_completed` table.
     *
     * @param userId the ID of the user whose total goals completed should be incremented.
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
}