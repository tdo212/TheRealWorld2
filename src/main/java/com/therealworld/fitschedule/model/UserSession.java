package com.therealworld.fitschedule.model;

/**
 * Singleton class representing the user session in the FitSchedule application.
 * This class holds information about the currently logged-in user and ensures that
 * only one session is active at a time. It stores the user ID and provides
 * methods to access and modify it.
 */
public class UserSession {

    /**
     * The single instance of the `UserSession` class.
     */
    private static UserSession instance;

    /**
     * The ID of the currently logged-in user.
     */
    private int userId;

    /**
     * Private constructor to prevent direct instantiation from outside the class.
     * Enforces the singleton pattern by limiting object creation to within the class itself.
     */
    private UserSession() {}

    /**
     * Provides access to the single instance of the `UserSession` class.
     * If the instance has not been created yet, it initializes a new one.
     *
     * @return the single `UserSession` instance.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Gets the ID of the currently logged-in user.
     *
     * @return the ID of the user as an integer.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the currently logged-in user.
     *
     * @param userId the new user ID to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
