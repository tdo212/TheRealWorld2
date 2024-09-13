package com.therealworld.fitschedule.model;

import java.sql.Date;
import java.sql.Time;

// Everything is a String to make it easier at the start to test if data is being read from a file
// and put into the database. We can change this later to the appropriate Data type for validation
// purposes

public class Schedule {
    private String dayOfWeek;
    private String eventName;
    private String eventDescription;
    private String eventStartTime;
    private String eventEndTime;

    public Schedule (String dayOfWeek, String eventName, String eventDescription, String eventStartTime, String eventEndTime ) {
        this.dayOfWeek = dayOfWeek;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }


    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    @Override
    public String toString() {
        return "currentSchedule {" +
                "dayOfWeek=" + dayOfWeek +
                ", eventName='" + eventName + '\'' +
               ", eventDescription='" + eventDescription + '\'' +
               ", eventStartTime=" + eventStartTime + '\'' +
               ", eventEndTime=" + eventEndTime +
               '}';
   }
}