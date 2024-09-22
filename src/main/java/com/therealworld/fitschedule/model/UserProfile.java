package com.therealworld.fitschedule.model;

public class UserProfile {

    private String height;
    private String username;
    private String email;
    private String weight;
    private String trainingFrequency;
    private String accountCreationDate;
    private String prefferedTrainingTime;

    public UserProfile(String username, String email,  String height, String weight, String trainingFrequency, String accountCreationDate, String prefferedTrainingTime) {
        this.username = username;
        this.email = email;
        this.height = height;
        this.weight = weight;
        this.trainingFrequency = trainingFrequency;
        this.accountCreationDate = accountCreationDate;
        this.prefferedTrainingTime = prefferedTrainingTime;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getTrainingFrequency() { return trainingFrequency; }
    public void setTrainingFrequency(String trainingFrequency) { this.trainingFrequency = trainingFrequency; }

    public String getAccountCreationDate() { return accountCreationDate; }
    public void setAccountCreationDate(String accountCreationDate) { this.accountCreationDate = accountCreationDate; }

    public String getPrefferedTrainingTime() { return prefferedTrainingTime; }
    public void setPrefferedTrainingTime(String prefferedTrainingTime) { this.prefferedTrainingTime = prefferedTrainingTime; }



}
