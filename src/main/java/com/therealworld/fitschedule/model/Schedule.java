package com.therealworld.fitschedule.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * A simple model class representing a Schedule with a day of the week, event name, description,
 * starting time and ending time.
 * This class provides getter and setter methods to access and modify user attributes.
 */
public class Schedule {

    /**
     * The id of the schedule entry.
     * This field is private, meaning it will only be accessible from inside the class it is declared in.
     */
    private int id;  // New field to uniquely identify a schedule entry
    /**
     * The day of the week of which the event takes place on.
     * This field is private, meaning it will only be accessible from inside the class it is declared in.
     * This field is final, meaning that its value cannot be changed once assigned.
     */
    private final StringProperty dayOfWeek;
    /**
     * The name of the event.
     * This field is private, meaning it will only be accessible from inside the class it is declared in.
     * This field is final, meaning that its value cannot be changed once assigned.
     */
    private final StringProperty eventName;
    /**
     * The description of the event.
     * This field is private, meaning it will only be accessible from inside the class it is declared in.
     * This field is final, meaning that its value cannot be changed once assigned.
     */
    private final StringProperty eventDescription;
    /**
     * The time, ideally an hour of the day, of which the event starts.
     * This field is private, meaning it will only be accessible from inside the class it is declared in.
     * This field is final, meaning that its value cannot be changed once assigned.
     */
    private final StringProperty eventStartTime;
    /**
     * The time, ideally an hour of the day, of which the event ends.
     * This field is private, meaning it will only be accessible from inside the class it is declared in.
     * This field is final, meaning that its value cannot be changed once assigned.
     */
    private final StringProperty eventEndTime;

    /**
     * Constructs a new `Schedule` instance with the provided attributes, without an ID for new entries
     * where the ID is auto-generated.
     *
     * @param dayOfWeek    the day of the week in which the event takes place on.
     * @param eventName    the name of the event.
     * @param eventDescription       a description of the event.
     * @param eventStartTime the hour during the day of which the event starts.
     * @param eventEndTime the hour during the day of which the event ends.
     */
    // Constructor without the ID, useful for new entries where ID is auto-generated
    public Schedule(String dayOfWeek, String eventName, String eventDescription, String eventStartTime, String eventEndTime) {
        this.dayOfWeek = new SimpleStringProperty(dayOfWeek);
        this.eventName = new SimpleStringProperty(eventName);
        this.eventDescription = new SimpleStringProperty(eventDescription);
        this.eventStartTime = new SimpleStringProperty(eventStartTime);
        this.eventEndTime = new SimpleStringProperty(eventEndTime);
    }

    /**
     * Constructs a new `Schedule` instance with the provided attributes, with an ID for existing entries.
     * @param id    the ID for the entry in the schedule.
     * @param dayOfWeek    the day of the week in which the event takes place on.
     * @param eventName    the name of the event.
     * @param eventDescription       a description of the event.
     * @param eventStartTime the hour during the day of which the event starts.
     * @param eventEndTime the hour during the day of which the event ends.
     */
    // Constructor with ID, useful for updating or referencing existing entries
    public Schedule(int id, String dayOfWeek, String eventName, String eventDescription, String eventStartTime, String eventEndTime) {
        this.id = id;
        this.dayOfWeek = new SimpleStringProperty(dayOfWeek);
        this.eventName = new SimpleStringProperty(eventName);
        this.eventDescription = new SimpleStringProperty(eventDescription);
        this.eventStartTime = new SimpleStringProperty(eventStartTime);
        this.eventEndTime = new SimpleStringProperty(eventEndTime);
    }

    // Getters and Setters for all fields
    /**
     * Gets the id of the schedule entry.
     *
     * @return the id of the schedule entry.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the schedule entry.
     * This method updates the private final `ID` field, which cannot be changed and is only
     * accessible from inside the class it is declared in.
     *
     * @param id the new id to set.
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * Gets the day of the week for the schedule entry.
     *
     * @return the day of the week for the schedule entry.
     */
    // For dayOfWeek
    public String getDayOfWeek() {
        return dayOfWeek.get();
    }
    /**
     * Sets the day of the week of the schedule entry.
     * This method updates the private final `dayOfWeek` field, which cannot be changed and is only
     * accessible from inside the class it is declared in.
     *
     * @param dayOfWeek the new day of the week to set.
     */
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek.set(dayOfWeek);
    }
    /**
     * @return the day of the week for the schedule entry.
     */
    public StringProperty dayOfWeekProperty() {
        return dayOfWeek;
    }
    /**
     * @return the day of the week for the schedule entry after getting it.
     */

    /**
     * Gets the name of the event for the schedule entry.
     *
     * @return a string that represents the name of the event.
     */
    // For eventName
    public String getEventName() {
        return eventName.get();
    }

    /**
     * Sets the name of the event of the schedule entry.
     * This method updates the private final `eventName` field, which cannot be changed and is only
     * accessible from inside the class it is declared in.
     *
     * @param eventName the new day of the week to set.
     */
    public void setEventName(String eventName) {
        this.eventName.set(eventName);
    }

    /**
     * The string property for the event name.
     * @return the property representing the name of the event for the schedule entry.
     */
    public StringProperty eventNameProperty() {
        return eventName;
    }

    /**
     * Gets the description of the event for the schedule entry.
     *
     * @return a string that represents a description of the event.
     */
    // For eventDescription
    public String getEventDescription() {
        return eventDescription.get();
    }

    /**
     * Sets the description of the event of the schedule entry.
     * This method updates the private final `eventDescription` field, which cannot be changed and is only
     * accessible from inside the class it is declared in.
     *
     * @param eventDescription the new day of the week to set.
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription.set(eventDescription);
    }

    /**
     * The string property for the event description.
     * @return the property representing the description of the event for the schedule entry.
     */
    public StringProperty eventDescriptionProperty() {
        return eventDescription;
    }

    /**
     * Gets the time during the day of which the event starts for the schedule entry.
     *
     * @return a string that represents the time during the day of which the event starts.
     */
    // For eventStartTime
    public String getEventStartTime() {
        return eventStartTime.get();
    }

    /**
     * Sets the starting time of the event of the schedule entry.
     * This method updates the private final `eventStartTime` field, which cannot be changed and is only
     * accessible from inside the class it is declared in.
     *
     * @param eventStartTime the new day of the week to set.
     */
    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime.set(eventStartTime);
    }

    /**
     * The string property for the starting time of the event.
     * @return the property representing the starting time of the event for the schedule entry.
     */
    public StringProperty eventStartTimeProperty() {
        return eventStartTime;
    }

    /**
     * Gets the time during the day of which the event ends for the schedule entry.
     *
     * @return a string that represents the time during the day of which the event ends.
     */
    // For eventEndTime
    public String getEventEndTime() {
        return eventEndTime.get();
    }

    /**
     * Sets the ending time of the event of the schedule entry.
     * This method updates the private final `eventEndTime` field, which cannot be changed and is only
     * accessible from inside the class it is declared in.
     *
     * @param eventEndTime the new day of the week to set.
     */
    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime.set(eventEndTime);
    }

    /**
     * The string property for the ending time of the event.
     * @return the property representing the ending time of the event for the schedule entry.
     */
    public StringProperty eventEndTimeProperty() {
        return eventEndTime;
    }

    /**
     * The string property for Monday.
     *
     * @return a StringProperty containing the event name if the current day of the week
     * is Monday, otherwise, returns an empty StringProperty if it isn't Monday.
     */
    // Custom properties for each day of the week
    public StringProperty mondayProperty() {
        return "Monday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    /**
     * The string property for Tuesday.
     *
     * @return a StringProperty containing the event name if the current day of the week
     * is Tuesday, otherwise, returns an empty StringProperty if it isn't Tuesday.
     */
    public StringProperty tuesdayProperty() {
        return "Tuesday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    /**
     * The string property for Wednesday.
     *
     * @return a StringProperty containing the event name if the current day of the week
     * is Wednesday, otherwise, returns an empty StringProperty if it isn't Wednesday.
     */
    public StringProperty wednesdayProperty() {
        return "Wednesday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    /**
     * The string property for Thursday.
     *
     * @return a StringProperty containing the event name if the current day of the week
     * is Thursday, otherwise, returns an empty StringProperty if it isn't Thursday.
     */
    public StringProperty thursdayProperty() {
        return "Thursday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    /**
     * The string property for Friday.
     *
     * @return a StringProperty containing the event name if the current day of the week
     * is Friday, otherwise, returns an empty StringProperty if it isn't Friday.
     */
    public StringProperty fridayProperty() {
        return "Friday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    /**
     * The string property for Saturday.
     *
     * @return a StringProperty containing the event name if the current day of the week
     * is Saturday, otherwise, returns an empty StringProperty if it isn't Saturday.
     */
    public StringProperty saturdayProperty() {
        return "Saturday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    /**
     * The string property for Sunday.
     *
     * @return a StringProperty containing the event name if the current day of the week
     * is Sunday, otherwise, returns an empty StringProperty if it isn't Sunday.
     */
    public StringProperty sundayProperty() {
        return "Sunday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    /**
     * Override method for debugging the Schedule table entries to a string
     *
     * @return a String containing the details of the schedule entry for debugging purposes.
     */
    // Improved toString method for better debugging output
    @Override
    public String toString() {
        return "Schedule {" +
                "id=" + id +
                ", dayOfWeek='" + dayOfWeek.get() + '\'' +
                ", eventName='" + eventName.get() + '\'' +
                ", eventDescription='" + eventDescription.get() + '\'' +
                ", eventStartTime='" + eventStartTime.get() + '\'' +
                ", eventEndTime='" + eventEndTime.get() + '\'' +
                '}';
    }
}
