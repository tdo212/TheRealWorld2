package com.therealworld.fitschedule.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ScheduleRow {
    private final SimpleStringProperty timeSlot;
    private final SimpleStringProperty monday;
    private final SimpleStringProperty tuesday;
    private final SimpleStringProperty wednesday;
    private final SimpleStringProperty thursday;
    private final SimpleStringProperty friday;
    private final SimpleStringProperty saturday;
    private final SimpleStringProperty sunday;

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

    public StringProperty timeSlotProperty() {
        return timeSlot;
    }

    public StringProperty mondayProperty() {
        return monday;
    }

    public StringProperty tuesdayProperty() {
        return tuesday;
    }

    public StringProperty wednesdayProperty() {
        return wednesday;
    }

    public StringProperty thursdayProperty() {
        return thursday;
    }

    public StringProperty fridayProperty() {
        return friday;
    }

    public StringProperty saturdayProperty() {
        return saturday;
    }

    public StringProperty sundayProperty() {
        return sunday;
    }
}
