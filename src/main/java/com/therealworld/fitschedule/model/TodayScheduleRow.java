package com.therealworld.fitschedule.model;

public class TodayScheduleRow {

    private String timeSlot;
    private String eventName;

    public TodayScheduleRow(String timeSlot, String eventName) {
        this.timeSlot = timeSlot;
        this.eventName = eventName;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}


