package com.therealworld.fitschedule.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The DayTracker class is responsible for tracking the current day
 * and managing the events that are scheduled for today or happening now.
 * It provides methods to determine if an event is happening today or at the current time.
 */
public class DayTracker {

    /**
     * Gets the current day of the week as a string.
     *
     * @return the current day of the week (e.g., "MONDAY", "TUESDAY").
     */
    public String getCurrentDay() {
        return LocalDate.now().getDayOfWeek().toString();
    }

    /**
     * Determines if the given schedule event is currently happening based on its start and end time.
     *
     * @param schedule the schedule event to check.
     * @return true if the event is currently happening, false otherwise.
     */
    public boolean isEventHappeningNow(Schedule schedule) {
        LocalTime now = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        try {
            LocalTime startTime = LocalTime.parse(schedule.getEventStartTime(), timeFormatter);
            LocalTime endTime = LocalTime.parse(schedule.getEventEndTime(), timeFormatter);

            // Check if the current time falls within the event's start and end time.
            return now.isAfter(startTime) && now.isBefore(endTime);
        } catch (Exception e) {
            System.out.println("Error parsing time for event: " + schedule.getEventName());
            return false;
        }
    }

    /**
     * Filters and returns a list of events that are happening today.
     *
     * @param allSchedules the list of all scheduled events.
     * @return a list of events that are scheduled for today.
     */
    public List<Schedule> getEventsForToday(List<Schedule> allSchedules) {
        return allSchedules.stream()
                .filter(this::isEventToday)  // Filter schedules based on whether they are today.
                .collect(Collectors.toList());
    }

    /**
     * Filters and returns a list of events that are happening today and are occurring at the current time.
     *
     * @param allSchedules the list of all scheduled events.
     * @return a list of events that are currently happening.
     */
    public List<Schedule> getEventsHappeningNow(List<Schedule> allSchedules) {
        String currentDay = getCurrentDay();

        // Filter events happening today and at the current time.
        return allSchedules.stream()
                .filter(schedule -> schedule.getDayOfWeek().equalsIgnoreCase(currentDay)
                        && isEventHappeningNow(schedule))
                .collect(Collectors.toList());
    }

    /**
     * Determines if a schedule event is scheduled for today based on the day of the week.
     *
     * @param schedule the schedule event to check.
     * @return true if the event is scheduled for today, false otherwise.
     */
    public boolean isEventToday(Schedule schedule) {
        // Compare the day of the event with the current day.
        return schedule.getDayOfWeek().equalsIgnoreCase(getCurrentDay());
    }

}
