package com.therealworld.fitschedule.model;

import java.time.LocalDate;

public class Goals {

    // Enum for goal types (WEEKLY, MONTHLY)
    public enum GoalType {
        WEEKLY,
        MONTHLY
    }

    private GoalType goalType;
    private int sessionsRequired;
    private int sessionsCompleted;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructor
    public Goals(GoalType goalType, int sessionsRequired, LocalDate startDate, LocalDate endDate) {
        this.goalType = goalType;
        this.sessionsRequired = sessionsRequired;
        this.sessionsCompleted = 0;  // Start with zero completed
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Method to add a completed session
    public void addCompletedSession() {
        if (sessionsCompleted < sessionsRequired) {
            sessionsCompleted++;
        }
    }

    // Method to check if the goal is met
    public boolean isGoalMet() {
        return sessionsCompleted >= sessionsRequired;
    }

    // Method to get the progress percentage
    public double getProgress() {
        return ((double) sessionsCompleted / sessionsRequired) * 100;
    }

    // Method to reset the goal (for a new week/month)
    public void resetGoal() {
        sessionsCompleted = 0;
    }

    // Getters and setters
    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public int getSessionsRequired() {
        return sessionsRequired;
    }

    public void setSessionsRequired(int sessionsRequired) {
        this.sessionsRequired = sessionsRequired;
    }

    public int getSessionsCompleted() {
        return sessionsCompleted;
    }

    public void setSessionsCompleted(int sessionsCompleted) {
        this.sessionsCompleted = sessionsCompleted;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
