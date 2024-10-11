
package com.therealworld.fitschedule.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        } catch (SQLException ex) {
            System.err.println("Error creating tables: " + ex.getMessage());
        }
    }

    public void createWeeklyScheduleTable(String weekStartDate, int userId) {
        String tableName = "weeklySchedule_" + weekStartDate.replace("-", "_");  // Create a unique table name using the start date
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "timeSlot TEXT NOT NULL, "
                + "Monday TEXT, "
                + "Tuesday TEXT, "
                + "Wednesday TEXT, "
                + "Thursday TEXT, "
                + "Friday TEXT, "
                + "Saturday TEXT, "
                + "Sunday TEXT, "
                + "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE, "
                + "UNIQUE (user_id, timeSlot))";  // Unique constraint for user and time slot

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

    // Populate the weekly schedule with time slots for a user
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

    // Insert a weekly event for a user
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

    // Retrieve the weekly schedule for a specific user and week offset
    public List<String[]> getWeeklyScheduleForWeek(int userId, int weekOffset) {
        List<String[]> schedule = new ArrayList<>();

        // Calculate the start and end dates for the specified week offset
        LocalDate startDate = LocalDate.now().plusWeeks(weekOffset).with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6); // Sunday of the same week

        String query = "SELECT timeSlot, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday " +
                "FROM weeklySchedule " +
                "WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
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
            System.err.println("Error retrieving weekly schedule for user " + userId + " with week offset " + weekOffset + ": " + ex.getMessage());
        }
        return schedule;
    }

    private boolean doesTableExist(String tableName) {
        String checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkTableQuery)) {
            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
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

    // Insert a schedule into the currentSchedule table
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


    // Get commitments for a specific day
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

    // Clear the schedule for a user
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
                        rs.getBoolean("goal_completed")  // goalCompleted
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
    public int countGoalsRemaining() {
        String sql = "SELECT COUNT(*) AS count FROM goals WHERE goal_completed = 0";
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
    public void updateGoalAsCompleted(int goalId) {
        String query = "UPDATE goals SET goal_completed = 1 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, goalId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
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


}
