package com.therealworld.fitschedule.model;

public class UserSession {

    private static UserSession instance;

    private int userId;  // This will store the logged-in user's ID

    // Private constructor prevents direct instantiation
    private UserSession() {}

    // Method to access the singleton instance
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Getter and Setter for userId
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}