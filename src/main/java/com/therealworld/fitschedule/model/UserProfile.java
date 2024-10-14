package com.therealworld.fitschedule.model;

public class UserProfile {
    private String username;
    private String email;
    private String phoneNumber;
    private String trainingFrequency;
    private String accountCreationDate;
    private String preferredTrainingTime;

//    , String trainingFrequency, String accountCreationDate, String preferredTrainingTime
    public UserProfile(String username, String email, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.email = email;
//        this.trainingFrequency = trainingFrequency;
//        this.accountCreationDate = accountCreationDate;
//        this.preferredTrainingTime = preferredTrainingTime;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTrainingFrequency() { return trainingFrequency; }
    public void setTrainingFrequency(String trainingFrequency) { this.trainingFrequency = trainingFrequency; }

    public String getAccountCreationDate() { return accountCreationDate; }
    public void setAccountCreationDate(String accountCreationDate) { this.accountCreationDate = accountCreationDate; }

    public String getPreferredTrainingTime() { return preferredTrainingTime; }
    public void setPreferredTrainingTime(String preferredTrainingTime) { this.preferredTrainingTime = preferredTrainingTime; }
}