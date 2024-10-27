package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class) // Initialize TestFX
public class DashboardControllerTest {

    private DashboardController dashboardController;
    private SqliteDAO scheduleDAO;
    private DayTracker dayTracker;

    @BeforeEach
    public void setup() {
        dashboardController = new DashboardController();
        scheduleDAO = mock(SqliteDAO.class);  // Mock database interactions
        dayTracker = new DayTracker();

        // Mock or initialize the UI components
        dashboardController.todaySchedule = new TableView<>();
        dashboardController.timeSlotColumn = new TableColumn<>();
        dashboardController.eventColumn = new TableColumn<>();

        // Inject dependencies
        dashboardController.scheduleDAO = scheduleDAO;
        dashboardController.dayTracker = dayTracker;
    }


    @Test
    public void testPopulateTodaySchedule_withEvents() {
        int userId = 1;
        List<Schedule> mockTodaySchedules = Arrays.asList(
                new Schedule("Monday", "Workout", "", "10:00 AM", "11:00 AM", true),
                new Schedule("Monday", "Study", "", "1:00 PM", "2:00 PM", false)
        );

        when(scheduleDAO.getCommitmentsForDay(userId, "Monday")).thenReturn(mockTodaySchedules);

        dashboardController.populateTodaySchedule(userId);

        // Assertions for expected results, like verifying table rows
        assertEquals(24, dashboardController.todaySchedule.getItems().size(), "Should match time slot count");
    }

    @Test
    public void testCalculateWorkoutHoursToday() {
        int userId = 1;
        String today = dayTracker.getCurrentDay();

        List<Schedule> mockWorkoutEvents = Arrays.asList(
                new Schedule("Monday", "Morning Run", "", "7:00 AM", "8:00 AM", true),
                new Schedule("Monday", "Evening Gym", "", "6:00 PM", "7:00 PM", true)
        );

        when(scheduleDAO.getCommitmentsForDay(userId, today)).thenReturn(mockWorkoutEvents);

        int workoutHours = dashboardController.calculateWorkoutHoursToday(userId);

        assertEquals(2, workoutHours, "Workout hours should match mock data count");
    }

    @Test
    public void testFindAvailableWorkoutSlots() {
        int userId = 1;
        int workoutHours = 2;
        String nextDay = dashboardController.getNextDay();

        List<Schedule> mockNextDaySchedule = Arrays.asList(
                new Schedule(nextDay, "Meeting", "", "10:00 AM", "11:00 AM", false),
                new Schedule(nextDay, "Lunch", "", "12:00 PM", "1:00 PM", false)
        );

        when(scheduleDAO.getCommitmentsForDay(userId, nextDay)).thenReturn(mockNextDaySchedule);

        List<String[]> availableSlots = dashboardController.findAvailableWorkoutSlots(userId, workoutHours);

        assertTrue(availableSlots.size() > 0, "Should return available slot options for workout.");
    }

    @Test
    public void testPopulateTodaySchedule_withNoEvents() {
        int userId = 1;
        when(scheduleDAO.getCommitmentsForDay(userId, "Monday")).thenReturn(Collections.emptyList());

        dashboardController.populateTodaySchedule(userId);

        // Verify all time slots are marked as free
        long freeSlots = dashboardController.todaySchedule.getItems().stream()
                .filter(row -> row.getEventName().isEmpty())
                .count();
        assertEquals(24, freeSlots, "All time slots should be free");
    }
    @Test
    public void testGetNextDay() {
        LocalDate today = LocalDate.now();
        String expectedNextDay = today.plusDays(1).getDayOfWeek().toString();
        assertEquals(expectedNextDay, dashboardController.getNextDay().toUpperCase(), "Next day calculation should match");
    }

    @Test
    public void testFindAvailableWorkoutSlots_noFreeSlots() {
        int userId = 1;
        int workoutHours = 1;
        String nextDay = dashboardController.getNextDay();

        List<Schedule> fullyBookedSchedule = Arrays.stream(dashboardController.timeSlots)
                .map(time -> new Schedule(nextDay, "Event", "", time, "", false))
                .collect(Collectors.toList());

        when(scheduleDAO.getCommitmentsForDay(userId, nextDay)).thenReturn(fullyBookedSchedule);

        List<String[]> availableSlots = dashboardController.findAvailableWorkoutSlots(userId, workoutHours);

        assertTrue(availableSlots.isEmpty(), "There should be no available slots for a fully booked day");
    }

    @Test
    public void testCalculateWorkoutHoursToday_mixedEventTypes() {
        int userId = 1;
        String today = dayTracker.getCurrentDay();

        List<Schedule> mixedEvents = Arrays.asList(
                new Schedule(today, "Workout", "", "7:00 AM", "", true),
                new Schedule(today, "Meeting", "", "9:00 AM", "", false),
                new Schedule(today, "Gym", "", "6:00 PM", "", true)
        );

        when(scheduleDAO.getCommitmentsForDay(userId, today)).thenReturn(mixedEvents);

        int workoutHours = dashboardController.calculateWorkoutHoursToday(userId);

        assertEquals(2, workoutHours, "Only fitness events should be counted");
    }

    @Test
    public void testCapitalizeFirstLetter() {
        assertEquals("Monday", dashboardController.capitalizeFirstLetter("monday"));
        assertEquals("Tuesday", dashboardController.capitalizeFirstLetter("TUESDAY"));
        assertEquals("", dashboardController.capitalizeFirstLetter(""));
        assertNull(dashboardController.capitalizeFirstLetter(null));
    }

}
