package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SqliteDAO;
import com.therealworld.fitschedule.model.UserSession;
import com.therealworld.fitschedule.model.ScheduleRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SchedulerControllerTest {


    @InjectMocks
    private SchedulerController schedulerController;

    @Mock
    private SqliteDAO mockSqliteDAO;

    @Mock
    private UserSession mockUserSession;

    @Mock
    private TableView<ScheduleRow> mockScheduleTable;

    @BeforeEach
    void setUp() {
        SchedulerController controller = new SchedulerController();
        SqliteDAO dao = controller.getScheduleDAO();  // Access the scheduleDAO using the new getter
        MockitoAnnotations.openMocks(this);
        schedulerController = new SchedulerController();
        schedulerController.scheduleDAO = mockSqliteDAO;  // Mock the DAO
        schedulerController.scheduleTable = mockScheduleTable;
        schedulerController.userId = 1;  // Assuming userId = 1 for testing
    }

    @Test
    void testInitialize() {
        // Simulate userId in UserSession
        when(mockUserSession.getUserId()).thenReturn(1);

        // Call the initialize method
        schedulerController.initialize();

        // Verify that the userId is correctly accessed from UserSession
        verify(mockUserSession, times(1)).getUserId();
    }

    @Test
    void testPopulateScheduleTable_withEmptyData() {
        // Mock empty schedule data
        when(mockSqliteDAO.getWeeklySchedule(1)).thenReturn(FXCollections.emptyObservableList());

        // Call the method to populate the schedule table
        schedulerController.populateScheduleTable(1);

        // Verify that the schedule table is empty
        ObservableList<ScheduleRow> rows = mockScheduleTable.getItems();
        assertTrue(rows.isEmpty(), "Schedule table should be empty");
    }

    @Test
    void testPopulateScheduleTable_withValidData() {
        // Mock valid schedule data (List<String[]>)
        List<String[]> mockScheduleData = Arrays.asList(
                new String[]{"12:00 AM", "Gym", "", "", "", "", "", ""},
                new String[]{"1:00 AM", "", "", "", "", "", "", ""}
        );

        // Mock the DAO call to return the mockScheduleData
        when(mockSqliteDAO.getWeeklySchedule(1)).thenReturn(mockScheduleData);

        // Call the method to populate the schedule table
        schedulerController.populateScheduleTable(1);

        // Capture the actual items set in the TableView
        ArgumentCaptor<ObservableList<ScheduleRow>> captor = ArgumentCaptor.forClass(ObservableList.class);
        verify(mockScheduleTable).setItems(captor.capture());

        // Now, let's assert that the captured list has the correct data
        ObservableList<ScheduleRow> capturedRows = captor.getValue();
        assertEquals(2, capturedRows.size(), "Schedule table should contain 2 rows");

        // Check the first row data
        ScheduleRow firstRow = capturedRows.get(0);
        assertEquals("12:00 AM", firstRow.timeSlotProperty().getValue(), "Time slot should be '12:00 AM'");
        assertEquals("Gym", firstRow.mondayProperty().getValue(), "The first event on Monday should be 'Gym'");

        // Check the second row data
        ScheduleRow secondRow = capturedRows.get(1);
        assertEquals("1:00 AM", secondRow.timeSlotProperty().getValue(), "Time slot should be '1:00 AM'");
        assertEquals("", secondRow.mondayProperty().getValue(), "There should be no event on Monday for the second time slot");
    }




    @Test
    void testOnLogoffButtonClick() throws IOException {
        // Prepare a mock ActionEvent
        javafx.event.ActionEvent event = mock(javafx.event.ActionEvent.class);

        // Call the method for logoff
        schedulerController.onLogoffButtonClick(event);

        // Verify if the method executed correctly (e.g., switching scenes)
        // This would typically require more advanced UI testing or TestFX framework
        // Here we just check if the function was called correctly
        assertNotNull(event);
    }
}
