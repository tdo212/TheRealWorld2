package com.therealworld.fitschedule.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {

    /**
     * This method returns the start date of the week (Monday) based on the provided offset.
     *
     * @param weekOffset The number of weeks to move from the current week (0 for current, -1 for previous, 1 for next week).
     * @return The start date (Monday) of the desired week in "yyyy-MM-dd" format.
     */
    public static String getWeekStartDate(int weekOffset) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Find the Monday of the current week
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // Adjust the startOfWeek date by the weekOffset (e.g., -1 for previous week, 1 for next week)
        LocalDate desiredWeekStart = startOfWeek.plusWeeks(weekOffset);

        // Format the date to "yyyy-MM-dd" and return it as a string
        return desiredWeekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


    // Helper method to capitalize the first letter and make the rest lowercase
    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

}