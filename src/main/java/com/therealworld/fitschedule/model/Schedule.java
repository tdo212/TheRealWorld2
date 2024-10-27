package com.therealworld.fitschedule.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * The Schedule class represents an event scheduled for a specific day and time. It includes
 * properties for the day of the week, event name, description, start time, end time, and
 * a flag indicating if the event is a fitness-related activity. The class provides constructors,
 * getters, and setters for these properties, along with JavaFX properties for data binding.
 */
public class Schedule {
    private int id;  // New field to uniquely identify a schedule entry
    private final StringProperty dayOfWeek;  // Property to store the day of the event
    private final StringProperty eventName;  // Property to store the name of the event
    private final StringProperty eventDescription;  // Property to store event description
    private final StringProperty eventStartTime;  // Property to store the start time of the event
    private final StringProperty eventEndTime;  // Property to store the end time of the event
    private final SimpleBooleanProperty isFitnessEvent;  // New field to track if the event is a fitness event

    /**
     * Constructor for creating a new Schedule without an ID.
     *
     * @param dayOfWeek        The day of the week for the event.
     * @param eventName        The name of the event.
     * @param eventDescription A description of the event.
     * @param eventStartTime   The start time of the event.
     * @param eventEndTime     The end time of the event.
     * @param isFitnessEvent   True if the event is a fitness-related activity; otherwise, false.
     */
    public Schedule(String dayOfWeek, String eventName, String eventDescription,
                    String eventStartTime, String eventEndTime, boolean isFitnessEvent) {
        this.dayOfWeek = new SimpleStringProperty(dayOfWeek);
        this.eventName = new SimpleStringProperty(eventName);
        this.eventDescription = new SimpleStringProperty(eventDescription);
        this.eventStartTime = new SimpleStringProperty(eventStartTime);
        this.eventEndTime = new SimpleStringProperty(eventEndTime);
        this.isFitnessEvent = new SimpleBooleanProperty(isFitnessEvent);
    }

    /**
     * Constructor for creating a Schedule with an ID, used for existing entries.
     *
     * @param id               The unique ID of the schedule.
     * @param dayOfWeek        The day of the week for the event.
     * @param eventName        The name of the event.
     * @param eventDescription A description of the event.
     * @param eventStartTime   The start time of the event.
     * @param eventEndTime     The end time of the event.
     * @param isFitnessEvent   True if the event is a fitness-related activity; otherwise, false.
     */
    public Schedule(int id, String dayOfWeek, String eventName, String eventDescription,
                    String eventStartTime, String eventEndTime, boolean isFitnessEvent) {
        this.id = id;
        this.dayOfWeek = new SimpleStringProperty(dayOfWeek);
        this.eventName = new SimpleStringProperty(eventName);
        this.eventDescription = new SimpleStringProperty(eventDescription);
        this.eventStartTime = new SimpleStringProperty(eventStartTime);
        this.eventEndTime = new SimpleStringProperty(eventEndTime);
        this.isFitnessEvent = new SimpleBooleanProperty(isFitnessEvent);
    }

    /**
     * Checks if the event is a fitness-related activity.
     *
     * @return True if the event is a fitness event; otherwise, false.
     */
    public boolean isFitnessEvent() {
        return isFitnessEvent.get();
    }

    /**
     * Sets whether the event is a fitness-related activity.
     *
     * @param isFitnessEvent True to mark the event as a fitness event; otherwise, false.
     */
    public void setFitnessEvent(boolean isFitnessEvent) {
        this.isFitnessEvent.set(isFitnessEvent);
    }

    /**
     * Returns the SimpleBooleanProperty for the fitness event, used for data binding.
     *
     * @return The SimpleBooleanProperty representing if the event is a fitness event.
     */
    public SimpleBooleanProperty fitnessEventProperty() {
        return isFitnessEvent;
    }

    // Getters and setters for other fields

    /**
     * Gets the unique ID of the schedule.
     *
     * @return The ID of the schedule.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the schedule.
     *
     * @param id The ID to set for the schedule.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the day of the week for the event.
     *
     * @return The day of the week.
     */
    public String getDayOfWeek() {
        return dayOfWeek.get();
    }

    /**
     * Sets the day of the week for the event.
     *
     * @param dayOfWeek The day of the week to set.
     */
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek.set(dayOfWeek);
    }

    /**
     * Returns the dayOfWeek property for data binding.
     *
     * @return The StringProperty representing the day of the week.
     */
    public StringProperty dayOfWeekProperty() {
        return dayOfWeek;
    }

    /**
     * Gets the name of the event.
     *
     * @return The event name.
     */
    public String getEventName() {
        return eventName != null ? eventName.get() : "";
    }

    /**
     * Sets the name of the event.
     *
     * @param eventName The name to set for the event.
     */
    public void setEventName(String eventName) {
        this.eventName.set(eventName);
    }

    /**
     * Returns the eventName property for data binding.
     *
     * @return The StringProperty representing the event name.
     */
    public StringProperty eventNameProperty() {
        return eventName;
    }

    /**
     * Gets the description of the event.
     *
     * @return The event description.
     */
    public String getEventDescription() {
        return eventDescription.get();
    }

    /**
     * Sets the description of the event.
     *
     * @param eventDescription The description to set for the event.
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription.set(eventDescription);
    }

    /**
     * Returns the eventDescription property for data binding.
     *
     * @return The StringProperty representing the event description.
     */
    public StringProperty eventDescriptionProperty() {
        return eventDescription;
    }

    /**
     * Gets the start time of the event.
     *
     * @return The event start time.
     */
    public String getEventStartTime() {
        return eventStartTime.get();
    }

    /**
     * Sets the start time of the event.
     *
     * @param eventStartTime The start time to set for the event.
     */
    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime.set(eventStartTime);
    }

    /**
     * Returns the eventStartTime property for data binding.
     *
     * @return The StringProperty representing the event start time.
     */
    public StringProperty eventStartTimeProperty() {
        return eventStartTime;
    }

    /**
     * Gets the end time of the event.
     *
     * @return The event end time.
     */
    public String getEventEndTime() {
        return eventEndTime.get();
    }

    /**
     * Sets the end time of the event.
     *
     * @param eventEndTime The end time to set for the event.
     */
    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime.set(eventEndTime);
    }

    /**
     * Returns the eventEndTime property for data binding.
     *
     * @return The StringProperty representing the event end time.
     */
    public StringProperty eventEndTimeProperty() {
        return eventEndTime;
    }

    /**
     * Returns a string representation of the Schedule object, useful for debugging.
     *
     * @return A string describing the schedule entry, including its ID, day of week,
     * event name, description, start time, end time, and fitness event status.
     */
    @Override
    public String toString() {
        return "Schedule {" +
                "id=" + id +
                ", dayOfWeek='" + dayOfWeek.get() + '\'' +
                ", eventName='" + eventName.get() + '\'' +
                ", eventDescription='" + eventDescription.get() + '\'' +
                ", eventStartTime='" + eventStartTime.get() + '\'' +
                ", eventEndTime='" + eventEndTime.get() + '\'' +
                ", isFitnessEvent=" + isFitnessEvent.get() +
                '}';
    }
}

