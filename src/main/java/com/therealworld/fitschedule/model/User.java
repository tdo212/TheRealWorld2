package com.therealworld.fitschedule.model;

/**
 * A simple model class representing a User with a username, password, email, and phone number.
 * This class provides getter and setter methods to access and modify user attributes.
 */
public class User {

    /**
     * The username of the user.
     * This field is static, meaning it will be shared among all instances of the class.
     */
    public static String username;

    /**
     * The password of the user. Stored as a plaintext value.
     * Ideally, this should be hashed and not stored directly in this form.
     */
    public String password;

    /**
     * The email address of the user.
     */
    public String email;

    /**
     * The phone number of the user.
     */
    public String phoneNumber;

    /**
     * Constructs a new `User` instance with the provided attributes.
     *
     * @param username    the username of the user.
     * @param password    the password of the user.
     * @param email       the email address of the user.
     * @param phoneNumber the phone number of the user.
     */
    public User(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username of the user.
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * This method updates the static `username` field, which is shared across all instances.
     *
     * @param username the new username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the new password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the new email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the user.
     *
     * @return the phone number of the user.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber the new phone number to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
