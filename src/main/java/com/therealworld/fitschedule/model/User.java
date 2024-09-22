package com.therealworld.fitschedule.model;

import org.mindrot.jbcrypt.BCrypt;

public class User {
    public static String username;
    public String hashedPassword;
    public String email;
    public String phoneNumber;

    public User(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters
    public static String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) {
        this.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()); // Hash password
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
