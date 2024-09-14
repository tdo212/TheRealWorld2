package com.therealworld.fitschedule.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Schedule {
    private int id;  // New field to uniquely identify a schedule entry
    private final StringProperty dayOfWeek;
    private final StringProperty eventName;
    private final StringProperty eventDescription;
    private final StringProperty eventStartTime;
    private final StringProperty eventEndTime;

    // Constructor without the ID, useful for new entries where ID is auto-generated
    public Schedule(String dayOfWeek, String eventName, String eventDescription, String eventStartTime, String eventEndTime) {
        this.dayOfWeek = new SimpleStringProperty(dayOfWeek);
        this.eventName = new SimpleStringProperty(eventName);
        this.eventDescription = new SimpleStringProperty(eventDescription);
        this.eventStartTime = new SimpleStringProperty(eventStartTime);
        this.eventEndTime = new SimpleStringProperty(eventEndTime);
    }

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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // For dayOfWeek
    public String getDayOfWeek() {
        return dayOfWeek.get();
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek.set(dayOfWeek);
    }

    public StringProperty dayOfWeekProperty() {
        return dayOfWeek;
    }

    // For eventName
    public String getEventName() {
        return eventName.get();
    }

    public void setEventName(String eventName) {
        this.eventName.set(eventName);
    }

    public StringProperty eventNameProperty() {
        return eventName;
    }

    // For eventDescription
    public String getEventDescription() {
        return eventDescription.get();
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription.set(eventDescription);
    }

    public StringProperty eventDescriptionProperty() {
        return eventDescription;
    }

    // For eventStartTime
    public String getEventStartTime() {
        return eventStartTime.get();
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime.set(eventStartTime);
    }

    public StringProperty eventStartTimeProperty() {
        return eventStartTime;
    }

    // For eventEndTime
    public String getEventEndTime() {
        return eventEndTime.get();
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime.set(eventEndTime);
    }

    public StringProperty eventEndTimeProperty() {
        return eventEndTime;
    }

    // Custom properties for each day of the week
    public StringProperty mondayProperty() {
        return "Monday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    public StringProperty tuesdayProperty() {
        return "Tuesday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    public StringProperty wednesdayProperty() {
        return "Wednesday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    public StringProperty thursdayProperty() {
        return "Thursday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    public StringProperty fridayProperty() {
        return "Friday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    public StringProperty saturdayProperty() {
        return "Saturday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

    public StringProperty sundayProperty() {
        return "Sunday".equals(dayOfWeek.get()) ? eventName : new SimpleStringProperty("");
    }

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
