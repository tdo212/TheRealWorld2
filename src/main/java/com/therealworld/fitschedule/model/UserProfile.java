package com.therealworld.fitschedule.model;

/**
 * A model class representing a user's profile in the FitSchedule application.
 * Contains attributes such as username, email, training frequency, account creation date,
 * and preferred training time. Provides getter and setter methods for these fields.
 */
public class UserProfile {

    /**
     * The username associated with the user's profile.
     */
    private String username;

    /**
     * The email address associated with the user's profile.
     */
    private String email;

    /**
     * The frequency of training sessions preferred by the user (e.g., "3 times a week").
     */
    private String trainingFrequency;

    /**
     * The date when the user account was created, stored as a string (e.g., "2023-01-01").
     */
    private String accountCreationDate;

    /**
     * The preferred time for the user's training sessions (e.g., "Morning", "Evening").
     */
    private String preferredTrainingTime;

    /**
     * Constructs a new `UserProfile` instance with the provided attributes.
     * This constructor is used when all details are available for the user.
     *
     * @param username             the username of the user.
     * @param email                the email address of the user.
     * @param trainingFrequency    the preferred training frequency of the user.
     * @param accountCreationDate  the date the user account was created.
     * @param preferredTrainingTime the preferred time of day for training sessions.
     */
    public UserProfile(String username, String email, String trainingFrequency, String accountCreationDate, String preferredTrainingTime) {
        this.username = username;
        this.email = email;
        this.trainingFrequency = trainingFrequency;
        this.accountCreationDate = accountCreationDate;
        this.preferredTrainingTime = preferredTrainingTime;
    }

    /**
     * Constructs a new `UserProfile` instance with only the username and email.
     * This constructor can be used when limited profile information is available.
     *
     * @param username the username of the user.
     * @param email    the email address of the user.
     */
    public UserProfile(String username, String email) {
        this.username = username;
        this.email = email;
        this.trainingFrequency = "";        // Default value for training frequency
        this.accountCreationDate = "";      // Default value for account creation date
        this.preferredTrainingTime = "";    // Default value for preferred training time
    }

    /**
     * Gets the username associated with the user's profile.
     *
     * @return the username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for the user's profile.
     *
     * @param username the new username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email address associated with the user's profile.
     *
     * @return the email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address for the user's profile.
     *
     * @param email the new email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the training frequency preferred by the user.
     *
     * @return the training frequency (e.g., "3 times a week").
     */
    public String getTrainingFrequency() {
        return trainingFrequency;
    }

    /**
     * Sets the training frequency preferred by the user.
     *
     * @param trainingFrequency the new training frequency to set.
     */
    public void setTrainingFrequency(String trainingFrequency) {
        this.trainingFrequency = trainingFrequency;
    }

    /**
     * Gets the date when the user account was created.
     *
     * @return the account creation date as a string.
     */
    public String getAccountCreationDate() {
        return accountCreationDate;
    }

    /**
     * Sets the date when the user account was created.
     *
     * @param accountCreationDate the new account creation date to set.
     */
    public void setAccountCreationDate(String accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    /**
     * Gets the preferred time of day for the user's training sessions.
     *
     * @return the preferred training time (e.g., "Morning").
     */
    public String getPreferredTrainingTime() {
        return preferredTrainingTime;
    }

    /**
     * Sets the preferred time of day for the user's training sessions.
     *
     * @param preferredTrainingTime the new preferred training time to set.
     */
    public void setPreferredTrainingTime(String preferredTrainingTime) {
        this.preferredTrainingTime = preferredTrainingTime;
    }
}
