package com.therealworld.fitschedule.model;

/**
 * Represents a user's profile information, including personal and training preferences.
 */
public class UserProfile {
    private String username;               // The username of the user
    private String email;                  // The email address of the user
    private String phoneNumber;            // The phone number of the user
    private String trainingFrequency;      // The user's preferred training frequency
    private String accountCreationDate;    // The date the user's account was created
    private String preferredTrainingTime;  // The user's preferred time for training

    /**
     * Constructs a UserProfile object with the specified username, email, and phone number.
     *
     * @param username    The username of the user.
     * @param email       The email address of the user.
     * @param phoneNumber The phone number of the user.
     */
    public UserProfile(String username, String email, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.email = email;
    }

    /**
     * Gets the username of the user.
     *
     * @return The user's username.
     */
    public String getUsername() { return username; }

    /**
     * Sets the username of the user.
     *
     * @param username The new username to set.
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Gets the phone number of the user.
     *
     * @return The user's phone number.
     */
    public String getPhoneNumber() { return phoneNumber; }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber The new phone number to set.
     */
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    /**
     * Gets the email address of the user.
     *
     * @return The user's email address.
     */
    public String getEmail() { return email; }

    /**
     * Sets the email address of the user.
     *
     * @param email The new email address to set.
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the user's preferred training frequency.
     *
     * @return The user's training frequency.
     */
    public String getTrainingFrequency() { return trainingFrequency; }

    /**
     * Sets the user's preferred training frequency.
     *
     * @param trainingFrequency The new training frequency to set.
     */
    public void setTrainingFrequency(String trainingFrequency) { this.trainingFrequency = trainingFrequency; }

    /**
     * Gets the account creation date of the user.
     *
     * @return The user's account creation date.
     */
    public String getAccountCreationDate() { return accountCreationDate; }

    /**
     * Sets the account creation date of the user.
     *
     * @param accountCreationDate The new account creation date to set.
     */
    public void setAccountCreationDate(String accountCreationDate) { this.accountCreationDate = accountCreationDate; }

    /**
     * Gets the user's preferred training time.
     *
     * @return The user's preferred training time.
     */
    public String getPreferredTrainingTime() { return preferredTrainingTime; }

    /**
     * Sets the user's preferred training time.
     *
     * @param preferredTrainingTime The new preferred training time to set.
     */
    public void setPreferredTrainingTime(String preferredTrainingTime) { this.preferredTrainingTime = preferredTrainingTime; }
}
