package com.therealworld.fitschedule.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Schedule {
    private int id;  // New field to uniquely identify a schedule entry
    private final StringProperty dayOfWeek;  // Property to store the day of the event
    private final StringProperty eventName;  // Property to store the name of the event
    private final StringProperty eventDescription;  // Property to store event description
    private final StringProperty eventStartTime;  // Property to store the start time of the event
    private final StringProperty eventEndTime;  // Property to store the end time of the event
    private final SimpleBooleanProperty isFitnessEvent;  // New field to track if the event is a fitness event

    // Constructor without the ID, useful for new entries where ID is auto-generated
    public Schedule(String dayOfWeek, String eventName, String eventDescription,
                    String eventStartTime, String eventEndTime, boolean isFitnessEvent) {
        this.dayOfWeek = new SimpleStringProperty(dayOfWeek);
        this.eventName = new SimpleStringProperty(eventName);
        this.eventDescription = new SimpleStringProperty(eventDescription);
        this.eventStartTime = new SimpleStringProperty(eventStartTime);
        this.eventEndTime = new SimpleStringProperty(eventEndTime);
        this.isFitnessEvent = new SimpleBooleanProperty(isFitnessEvent);
    }

    // Constructor with ID, useful for updating or referencing existing entries
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

    // Getter and setter for isFitnessEvent property
    public boolean isFitnessEvent() {
        return isFitnessEvent.get();  // Returns the fitness event status
    }

    public void setFitnessEvent(boolean isFitnessEvent) {
        this.isFitnessEvent.set(isFitnessEvent);  // Updates the fitness event status
    }

    public SimpleBooleanProperty fitnessEventProperty() {
        return isFitnessEvent;  // Returns the fitness event property for binding
    }

    // Getters and setters for other fields

    public int getId() {
        return id;  // Returns the ID of the schedule
    }

    public void setId(int id) {
        this.id = id;  // Sets the ID of the schedule
    }

    public String getDayOfWeek() {
        return dayOfWeek.get();  // Returns the day of the event
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek.set(dayOfWeek);  // Sets the day of the event
    }

    public StringProperty dayOfWeekProperty() {
        return dayOfWeek;  // Returns the day property for binding
    }

    public String getEventName() {
        return eventName != null ? eventName.get() : "";  // Avoids potential NPE
    }

    public void setEventName(String eventName) {
        this.eventName.set(eventName);  // Sets the event name
    }

    public StringProperty eventNameProperty() {
        return eventName;  // Returns the event name property for binding
    }

    public String getEventDescription() {
        return eventDescription.get();  // Returns the event description
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription.set(eventDescription);  // Sets the event description
    }

    public StringProperty eventDescriptionProperty() {
        return eventDescription;  // Returns the event description property for binding
    }

    public String getEventStartTime() {
        return eventStartTime.get();  // Returns the event start time
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime.set(eventStartTime);  // Sets the event start time
    }

    public StringProperty eventStartTimeProperty() {
        return eventStartTime;  // Returns the event start time property for binding
    }

    public String getEventEndTime() {
        return eventEndTime.get();  // Returns the event end time
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime.set(eventEndTime);  // Sets the event end time
    }

    public StringProperty eventEndTimeProperty() {
        return eventEndTime;  // Returns the event end time property for binding
    }

    // Custom toString method for better debugging output
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
