package com.therealworld.fitschedule.model;

/**
 * Represents a row in today's schedule.
 * Each row contains a time slot and the corresponding event name for that time slot.
 */
public class TodayScheduleRow {

    /**
     * The time slot for this row (e.g., "10:00 AM - 11:00 AM").
     */
    private String timeSlot;

    /**
     * The name of the event scheduled for this time slot.
     */
    private String eventName;

    /**
     * Constructs a new TodayScheduleRow object with the specified time slot and event name.
     *
     * @param timeSlot the time slot for this row (e.g., "10:00 AM - 11:00 AM").
     * @param eventName the name of the event scheduled for this time slot.
     */
    public TodayScheduleRow(String timeSlot, String eventName) {
        this.timeSlot = timeSlot;
        this.eventName = eventName;
    }

    /**
     * Gets the time slot of this schedule row.
     *
     * @return the time slot for this row.
     */
    public String getTimeSlot() {
        return timeSlot;
    }

    /**
     * Sets the time slot for this schedule row.
     *
     * @param timeSlot the new time slot to set (e.g., "10:00 AM - 11:00 AM").
     */
    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    /**
     * Gets the event name associated with this time slot.
     *
     * @return the name of the event.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the event name associated with this time slot.
     *
     * @param eventName the new event name to set.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
