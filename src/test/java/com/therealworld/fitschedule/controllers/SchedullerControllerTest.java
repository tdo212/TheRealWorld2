package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SqliteDAO;
import com.therealworld.fitschedule.model.ScheduleRow;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class SchedulerControllerTest {

    @InjectMocks
    private SchedulerController schedulerController;

    @Mock
    private SqliteDAO mockSqliteDAO;

    private TableView<ScheduleRow> mockScheduleTable;

    @BeforeAll
    static void initJFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await(5, TimeUnit.SECONDS);
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        MockitoAnnotations.openMocks(this);

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            mockScheduleTable = new TableView<>();
            schedulerController = new SchedulerController();
            schedulerController.scheduleDAO = mockSqliteDAO;  // Mock the DAO
            schedulerController.scheduleTable = mockScheduleTable;  // Use real TableView
            schedulerController.userId = 1;  // Assuming userId = 1 for testing

            // Initialize the table columns properly
            schedulerController.timeSlotColumn = new TableColumn<>("Time Slot");
            schedulerController.mondayColumn = new TableColumn<>("Monday");
            schedulerController.tuesdayColumn = new TableColumn<>("Tuesday");
            schedulerController.wednesdayColumn = new TableColumn<>("Wednesday");
            schedulerController.thursdayColumn = new TableColumn<>("Thursday");
            schedulerController.fridayColumn = new TableColumn<>("Friday");
            schedulerController.saturdayColumn = new TableColumn<>("Saturday");
            schedulerController.sundayColumn = new TableColumn<>("Sunday");

            mockScheduleTable.getColumns().addAll(
                    schedulerController.timeSlotColumn,
                    schedulerController.mondayColumn,
                    schedulerController.tuesdayColumn,
                    schedulerController.wednesdayColumn,
                    schedulerController.thursdayColumn,
                    schedulerController.fridayColumn,
                    schedulerController.saturdayColumn,
                    schedulerController.sundayColumn
            );

            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    void testInitialize() {
        // Simulate userId in UserSession
        schedulerController.userId = 1;

        // Call the initialize method
        Platform.runLater(() -> schedulerController.initialize());

        // Verify if the table columns were bound (they should be initialized by now)
        Assertions.assertNotNull(schedulerController.timeSlotColumn, "Time slot column should be initialized");
        Assertions.assertNotNull(schedulerController.mondayColumn, "Monday column should be initialized");
    }

    @Test
    void testPopulateScheduleTable_withValidData() {
        // Mock valid schedule data (List<String[]>)
        List<String[]> mockScheduleData = Arrays.asList(
                new String[]{"12:00 AM", "Gym", "", "", "", "", "", ""},
                new String[]{"1:00 AM", "", "", "", "", "", "", ""}
        );

        // Mock the DAO call to return the mockScheduleData
        Mockito.when(mockSqliteDAO.getWeeklySchedule(1)).thenReturn(mockScheduleData);

        // Call the method to populate the schedule table
        Platform.runLater(() -> schedulerController.populateScheduleTable(1));

        // Capture the actual items set in the TableView
        ObservableList<ScheduleRow> items = mockScheduleTable.getItems();
        Assertions.assertEquals(2, items.size(), "Schedule table should contain 2 rows");

        // Check the first row data
        ScheduleRow firstRow = items.get(0);
        Assertions.assertEquals("12:00 AM", firstRow.timeSlotProperty().getValue(), "Time slot should be '12:00 AM'");
        Assertions.assertEquals("Gym", firstRow.mondayProperty().getValue(), "The first event on Monday should be 'Gym'");

        // Check the second row data
        ScheduleRow secondRow = items.get(1);
        Assertions.assertEquals("1:00 AM", secondRow.timeSlotProperty().getValue(), "Time slot should be '1:00 AM'");
        Assertions.assertEquals("", secondRow.mondayProperty().getValue(), "There should be no event on Monday for the second time slot");
    }

    @Test
    void testPopulateScheduleTable_withEmptyData() {
        // Mock empty schedule data
        Mockito.when(mockSqliteDAO.getWeeklySchedule(1)).thenReturn(FXCollections.observableArrayList());

        // Call the method to populate the schedule table
        Platform.runLater(() -> schedulerController.populateScheduleTable(1));

        // Verify that the schedule table is empty
        Assertions.assertTrue(mockScheduleTable.getItems().isEmpty(), "Schedule table should be empty");
    }

    @Test
    void testOnLogoffButtonClick() throws IOException {
        // Mock the action event
        javafx.event.ActionEvent event = Mockito.mock(javafx.event.ActionEvent.class);
        Node node = Mockito.mock(Node.class);
        Scene scene = Mockito.mock(Scene.class);
        Stage stage = Mockito.mock(Stage.class);

        // Simulate the node hierarchy for event
        Mockito.when(event.getSource()).thenReturn(node);
        Mockito.when(node.getScene()).thenReturn(scene);
        Mockito.when(scene.getWindow()).thenReturn(stage);

        // Call the logoff button click
        Platform.runLater(() -> {
            try {
                schedulerController.onLogoffButtonClick(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Verify the stage close method was called
        Mockito.verify(stage, Mockito.times(1)).close();
    }
}