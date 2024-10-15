package com.therealworld.fitschedule.model;

public class Goal {
    private int goalId;
    private String goalType;
    private int goalDuration;
    private String goalPeriod;
    private String goalDescription;
    private boolean goalCompleted;
    private int goalProgress;

    // Constructor
    public Goal(int goalId, String goalType, int goalDuration, String goalPeriod, String goalDescription, boolean goalCompleted, int goalProgress) {
        this.goalId = goalId;
        this.goalType = goalType;
        this.goalDuration = goalDuration;
        this.goalPeriod = goalPeriod;
        this.goalDescription = goalDescription;
        this.goalCompleted = goalCompleted;
        this.goalProgress = this.goalProgress;
    }

    // Getters and Setters
    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }

    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }

    public int getGoalDuration() { return goalDuration; }
    public void setGoalDuration(int goalDuration) { this.goalDuration = goalDuration; }

    public String getGoalPeriod() { return goalPeriod; }
    public void setGoalPeriod(String goalPeriod) { this.goalPeriod = goalPeriod; }

    public String getGoalDescription() { return goalDescription; }
    public void setGoalDescription(String goalDescription) { this.goalDescription = goalDescription; }

    public boolean isGoalCompleted() { return goalCompleted; }
    public void setGoalCompleted(boolean goalCompleted) { this.goalCompleted = goalCompleted; }

    public int getGoalProgress() {
        return goalProgress;
    }

    public void setGoalProgress(int goalProgress) {
        this.goalProgress = goalProgress;
    }

    @Override
    public String toString() {
        return "Goal{id=" + goalId +
                ", type='" + goalType + '\'' +
                ", duration=" + goalDuration +
                ", period='" + goalPeriod + '\'' +
                ", description='" + goalDescription + '\'' +
                ", completed=" + goalCompleted +
                ", progress=" + goalProgress +
                '}';
    }
}
