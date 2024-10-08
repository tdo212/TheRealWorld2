package com.therealworld.fitschedule.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The ScheduleRow class represents a row in the weekly schedule.
 * Each row contains the time slot and activities for each day of the week.
 */
public class ScheduleRow {

    /**
     * The time slot for this row (e.g., "08:00 AM - 09:00 AM").
     */
    private final SimpleStringProperty timeSlot;

    /**
     * The schedule for Monday in this time slot.
     */
    private final SimpleStringProperty monday;

    /**
     * The schedule for Tuesday in this time slot.
     */
    private final SimpleStringProperty tuesday;

    /**
     * The schedule for Wednesday in this time slot.
     */
    private final SimpleStringProperty wednesday;

    /**
     * The schedule for Thursday in this time slot.
     */
    private final SimpleStringProperty thursday;

    /**
     * The schedule for Friday in this time slot.
     */
    private final SimpleStringProperty friday;

    /**
     * The schedule for Saturday in this time slot.
     */
    private final SimpleStringProperty saturday;

    /**
     * The schedule for Sunday in this time slot.
     */
    private final SimpleStringProperty sunday;

    /**
     * Constructs a new ScheduleRow object with the provided schedule for each day of the week.
     *
     * @param timeSlot   the time slot for this row (e.g., "08:00 AM - 09:00 AM").
     * @param monday     the schedule for Monday.
     * @param tuesday    the schedule for Tuesday.
     * @param wednesday  the schedule for Wednesday.
     * @param thursday   the schedule for Thursday.
     * @param friday     the schedule for Friday.
     * @param saturday   the schedule for Saturday.
     * @param sunday     the schedule for Sunday.
     */
    public ScheduleRow(String timeSlot, String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday) {
        this.timeSlot = new SimpleStringProperty(timeSlot);
        this.monday = new SimpleStringProperty(monday);
        this.tuesday = new SimpleStringProperty(tuesday);
        this.wednesday = new SimpleStringProperty(wednesday);
        this.thursday = new SimpleStringProperty(thursday);
        this.friday = new SimpleStringProperty(friday);
        this.saturday = new SimpleStringProperty(saturday);
        this.sunday = new SimpleStringProperty(sunday);
    }

    /**
     * Gets the time slot property.
     *
     * @return the time slot property for this row.
     */
    public StringProperty timeSlotProperty() {
        return timeSlot;
    }

    /**
     * Gets the Monday property for this time slot.
     *
     * @return the Monday property.
     */
    public StringProperty mondayProperty() {
        return monday;
    }

    /**
     * Gets the Tuesday property for this time slot.
     *
     * @return the Tuesday property.
     */
    public StringProperty tuesdayProperty() {
        return tuesday;
    }

    /**
     * Gets the Wednesday property for this time slot.
     *
     * @return the Wednesday property.
     */
    public StringProperty wednesdayProperty() {
        return wednesday;
    }

    /**
     * Gets the Thursday property for this time slot.
     *
     * @return the Thursday property.
     */
    public StringProperty thursdayProperty() {
        return thursday;
    }

    /**
     * Gets the Friday property for this time slot.
     *
     * @return the Friday property.
     */
    public StringProperty fridayProperty() {
        return friday;
    }

    /**
     * Gets the Saturday property for this time slot.
     *
     * @return the Saturday property.
     */
    public StringProperty saturdayProperty() {
        return saturday;
    }

    /**
     * Gets the Sunday property for this time slot.
     *
     * @return the Sunday property.
     */
    public StringProperty sundayProperty() {
        return sunday;
    }
}
