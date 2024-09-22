package com.therealworld.fitschedule.model;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
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
        createWeeklyScheduleTable();  // Create the weekly schedule table
        populateTimeSlots();  // Populate time slots in the weekly schedule table
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
                            "FOREIGN KEY(user_id) REFERENCES users(id))"
            );
            // Create userProfile table
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS userProfile (" +
                            "user_id INTEGER PRIMARY KEY, " +
                            "username VARCHAR(50) NOT NULL, " +
                            "email VARCHAR(50) NOT NULL, " +
                            "trainingFrequency VARCHAR(50), " +
                            "accountCreationDate VARCHAR(50), " +
                            "preferredTrainingTime VARCHAR(50), " +
                            "FOREIGN KEY(user_id) REFERENCES users(id))"
            );
            System.out.println("userProfile table created or already exists.");

            System.out.println("Goals table created or already exists.");
        } catch (SQLException ex) {
            System.err.println("Error creating tables: " + ex.getMessage());
        }
    }
    public void createProfile(int userId, String username, String email) {
        String query = "INSERT INTO userProfile (user_id, username, email, trainingFrequency, accountCreationDate, preferredTrainingTime) VALUES (?, ?, ?, '3 times a week', '2024-01-01', 'Morning')";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            System.out.println("Profile created for user: " + username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Create the weekly schedule table linked with the users table
    private void createWeeklyScheduleTable() {
        try (Statement stmt = connection.createStatement()) {
            // Create the weekly schedule table with time slots, user_id, and days of the week columns
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS weeklySchedule (" +
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
                            "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)"
            );
            System.out.println("Weekly schedule table created or already exists.");
        } catch (SQLException ex) {
            System.err.println("Error creating weekly schedule table: " + ex.getMessage());
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
    private void populateTimeSlots() {
        String[] timeSlots = {
                "12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM",
                "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
                "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"
        };

        String insertQuery = "INSERT OR IGNORE INTO weeklySchedule (timeSlot, user_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            List<Integer> userIds = getAllUserIds();  // Get all user IDs to populate for each user
            for (int userId : userIds) {
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

    // Retrieve the full weekly schedule for a specific user
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


    public void addUser(String username, String password, String email, String phoneNumber) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO users (username, password, email, phoneNumber) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            pstmt.setString(4, phoneNumber);
            pstmt.executeUpdate();

            // Get the generated user ID
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                // Create a profile for the new user
                createProfile(userId, username, email);
            }

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

    // Retrieve schedules for a specific user
    public List<Schedule> getScheduleForUser(int userId) {
        List<Schedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM currentSchedule WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
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
            System.err.println("Error retrieving schedules: " + ex.getMessage());
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
    public void addGoal(int userId, String goalType, int goalDuration, String goalPeriod, String goalDescription) {
        String query = "INSERT INTO goals (user_id, goal_type, goal_duration, goal_period, goal_description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, goalType);
            pstmt.setInt(3, goalDuration);
            pstmt.setString(4, goalPeriod);
            pstmt.setString(5, goalDescription);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all goals for a user
    public List<String> getGoalsForUser(int userId) {
        List<String> goals = new ArrayList<>();
        String query = "SELECT * FROM goals WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                goals.add(rs.getString("goal_description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }

    // Delete a goal by ID
    public void deleteGoal(int goalId) {
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

    public UserProfile fetchProfileDetails(int userId) {
        String sql = "SELECT * FROM userProfile WHERE user_id = ?";
        UserProfile userProfile = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                userProfile = new UserProfile(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("trainingFrequency"),
                        rs.getString("accountCreationDate"),
                        rs.getString("preferredTrainingTime")
                );
            } else {
                System.out.println("No profile found for user ID: " + userId);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching profile: " + e.getMessage());
        }

        return userProfile;
    }

}
