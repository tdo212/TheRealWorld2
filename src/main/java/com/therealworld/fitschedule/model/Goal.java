package com.therealworld.fitschedule.model;
/**
 * The Goal class represents a fitness goal with properties such as type, duration,
 * description, and completion status. It provides a way to store and manage goals
 * for users in the FitSync application.
 */
public class Goal {
    /**
     * Unique identifier for the goal.
     */
    private int goalId;
    /**
     * Type of the goal (e.g., 'Cardio', 'Gym').
     */
    private String goalType;
    /**
     * Duration of the goal in terms of the specified period (e.g., 4 weeks).
     */
    private int goalDuration;
    /**
     * The time period of the goal (e.g., 'Hours per week', 'Days per week').
     */
    private String goalPeriod;
    /**
     * A detailed description of the goal.
     */
    private String goalDescription;
    /**
     * Status indicating whether the goal is completed (true if completed, false otherwise).
     */
    private boolean goalCompleted;

    /**
     * Constructor to create a new Goal object with specified properties.
     *
     * @param goalId          the unique identifier for the goal
     * @param goalType        the type of the goal
     * @param goalDuration    the duration of the goal
     * @param goalPeriod      the period for the goal (e.g., 'Days per week')
     * @param goalDescription a description of the goal
     * @param goalCompleted   the completion status of the goal (true if completed, false otherwise)
     */
    public Goal(int goalId, String goalType, int goalDuration, String goalPeriod, String goalDescription, boolean goalCompleted) {
        this.goalId = goalId;
        this.goalType = goalType;
        this.goalDuration = goalDuration;
        this.goalPeriod = goalPeriod;
        this.goalDescription = goalDescription;
        this.goalCompleted = goalCompleted;
    }

    /**
     * Getter and Setter Methods for the goalID, which links with variables of the same name in the database.
     */
    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }
    /**
     * Getter and Setter Methods for the goalType, which links with variables of the same name in the database.
     */
    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }
    /**
     * Getter and Setter Methods for the goalDuration, which links with variables of the same name in the database.
     */
    public int getGoalDuration() { return goalDuration; }
    public void setGoalDuration(int goalDuration) { this.goalDuration = goalDuration; }
    /**
     * Getter and Setter Methods for the goalPeriod, which links with variables of the same name in the database.
     */
    public String getGoalPeriod() { return goalPeriod; }
    public void setGoalPeriod(String goalPeriod) { this.goalPeriod = goalPeriod; }
    /**
     * Getter and Setter Methods for the goalDescription, which links with variables of the same name in the database.
     */
    public String getGoalDescription() { return goalDescription; }
    public void setGoalDescription(String goalDescription) { this.goalDescription = goalDescription; }
    /**
     * Getter and Setter Methods for the goalCompleted, which links with variables of the same name in the database.
     */
    public boolean isGoalCompleted() { return goalCompleted; }
    public void setGoalCompleted(boolean goalCompleted) { this.goalCompleted = goalCompleted; }
}
