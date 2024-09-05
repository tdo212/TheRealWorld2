package com.therealworld.fitschedule.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqliteUserDAO {

    private Connection connection;

    public SqliteUserDAO() {
        connection = SqliteConnection.getInstance(); // Assuming SqliteConnection is set up correctly
        createTable();
    }

    private void createTable() {
        try {
            // Create the users table if it doesn't exist
            String query = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username VARCHAR(50) UNIQUE NOT NULL,"
                    + "password VARCHAR(50) NOT NULL,"
                    + "email VARCHAR(50) NOT NULL,"
                    + "phoneNumber VARCHAR(15) NOT NULL"
                    + ")";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser(String username, String password, String email, String phoneNumber) {
        String query = "INSERT INTO users (username, password, email, phoneNumber) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, phoneNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
