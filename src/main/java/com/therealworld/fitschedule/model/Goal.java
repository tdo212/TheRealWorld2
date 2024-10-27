package com.therealworld.fitschedule.model;

/**
 * The Goal class represents a user-defined goal with specific parameters such as type,
 * duration, period, and description. It tracks the goal's completion status and progress
 * over time. This class provides getters and setters for each property, enabling easy
 * management and updating of goals within the application.
 */
public class Goal {
    private int goalId;  // Unique identifier for the goal
    private String goalType;  // Type of the goal, e.g., "Fitness" or "Study"
    private int goalDuration;  // Duration of the goal in relevant units (e.g., hours, days)
    private String goalPeriod;  // Period over which the goal applies, e.g., "Daily", "Weekly"
    private String goalDescription;  // Description of the goal's purpose or details
    private boolean goalCompleted;  // Indicates if the goal has been completed
    private int goalProgress;  // Tracks progress made towards the goal

    /**
     * Constructs a new Goal with the specified parameters.
     *
     * @param goalId         The unique ID of the goal.
     * @param goalType       The type or category of the goal.
     * @param goalDuration   The target duration or quantity to achieve the goal.
     * @param goalPeriod     The period for the goal's tracking, such as daily or weekly.
     * @param goalDescription A description of the goal.
     * @param goalCompleted  True if the goal has been completed, false otherwise.
     * @param goalProgress   Current progress made towards the goal.
     */
    public Goal(int goalId, String goalType, int goalDuration, String goalPeriod, String goalDescription, boolean goalCompleted, int goalProgress) {
        this.goalId = goalId;
        this.goalType = goalType;
        this.goalDuration = goalDuration;
        this.goalPeriod = goalPeriod;
        this.goalDescription = goalDescription;
        this.goalCompleted = goalCompleted;
        this.goalProgress = goalProgress;
    }

    /**
     * Gets the unique ID of the goal.
     *
     * @return The goal ID.
     */
    public int getGoalId() {
        return goalId;
    }

    /**
     * Sets the unique ID of the goal.
     *
     * @param goalId The goal ID to set.
     */
    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    /**
     * Gets the type of the goal.
     *
     * @return The goal type.
     */
    public String getGoalType() {
        return goalType;
    }

    /**
     * Sets the type of the goal.
     *
     * @param goalType The goal type to set.
     */
    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    /**
     * Gets the target duration of the goal.
     *
     * @return The goal duration.
     */
    public int getGoalDuration() {
        return goalDuration;
    }

    /**
     * Sets the target duration of the goal.
     *
     * @param goalDuration The goal duration to set.
     */
    public void setGoalDuration(int goalDuration) {
        this.goalDuration = goalDuration;
    }

    /**
     * Gets the period for tracking the goal, such as daily or weekly.
     *
     * @return The goal period.
     */
    public String getGoalPeriod() {
        return goalPeriod;
    }

    /**
     * Sets the period for tracking the goal.
     *
     * @param goalPeriod The goal period to set.
     */
    public void setGoalPeriod(String goalPeriod) {
        this.goalPeriod = goalPeriod;
    }

    /**
     * Gets the description of the goal.
     *
     * @return The goal description.
     */
    public String getGoalDescription() {
        return goalDescription;
    }

    /**
     * Sets the description of the goal.
     *
     * @param goalDescription The goal description to set.
     */
    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }

    /**
     * Checks if the goal has been completed.
     *
     * @return True if the goal is completed, false otherwise.
     */
    public boolean isGoalCompleted() {
        return goalCompleted;
    }

    /**
     * Sets the completion status of the goal.
     *
     * @param goalCompleted True to mark the goal as completed, false otherwise.
     */
    public void setGoalCompleted(boolean goalCompleted) {
        this.goalCompleted = goalCompleted;
    }

    /**
     * Gets the current progress made towards achieving the goal.
     *
     * @return The goal progress.
     */
    public int getGoalProgress() {
        return goalProgress;
    }

    /**
     * Sets the current progress towards the goal.
     *
     * @param goalProgress The progress value to set.
     */
    public void setGoalProgress(int goalProgress) {
        this.goalProgress = goalProgress;
    }

    /**
     * Returns a string representation of the Goal object, useful for debugging.
     *
     * @return A string describing the goal, including its ID, type, duration,
     * period, description, completion status, and progress.
     */
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
