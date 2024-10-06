package com.therealworld.fitschedule.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DayTracker {

    // Returns the current day of the week as a string
    public String getCurrentDay() {
        return LocalDate.now().getDayOfWeek().toString();
    }

    // Returns whether the given schedule event is happening at the current time
    public boolean isEventHappeningNow(Schedule schedule) {
        // Get current time
        LocalTime now = LocalTime.now();

        // Parse start and end times from the schedule
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        try {
            LocalTime startTime = LocalTime.parse(schedule.getEventStartTime(), timeFormatter);
            LocalTime endTime = LocalTime.parse(schedule.getEventEndTime(), timeFormatter);

            // Check if the current time falls within the event's time range
            return now.isAfter(startTime) && now.isBefore(endTime);
        } catch (Exception e) {
            System.out.println("Error parsing time for event: " + schedule.getEventName());
            return false;
        }
    }

    // Filters and returns a list of events happening today
    public List<Schedule> getEventsForToday(List<Schedule> allSchedules) {
        return allSchedules.stream()
                .filter(this::isEventToday)  // Filter schedules based on whether they are today
                .collect(Collectors.toList());
    }

    // Filters the list of events to find events happening today and now
    public List<Schedule> getEventsHappeningNow(List<Schedule> allSchedules) {
        String currentDay = getCurrentDay();

        // Filter events happening today and at the current time
        return allSchedules.stream()
                .filter(schedule -> schedule.getDayOfWeek().equalsIgnoreCase(currentDay)
                        && isEventHappeningNow(schedule))
                .collect(Collectors.toList());
    }

    // Method to check if a schedule event is happening today
    public boolean isEventToday(Schedule schedule) {
        // Compare the day of the event with the current day
        return schedule.getDayOfWeek().equalsIgnoreCase(getCurrentDay());
    }
}
