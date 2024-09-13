package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.ScheduleDAO;
import com.therealworld.fitschedule.model.Schedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.layout.VBox;


import java.io.IOException;

public class SchedulerController {
    @FXML
    private Button logoffButton;
    @FXML
    private Button scheduleButton;
    @FXML
    private Button previousWeekButton;
    @FXML
    private Button currentWeekButton;
    @FXML
    private Button nextWeekButton;

//    Creating database object
    private ScheduleDAO scheduleDAO = new ScheduleDAO();

    @FXML
    protected void onLogoffButtonClick(ActionEvent event) throws IOException {

        // Ben M's log out code for dashboard which serves the same purpose, don't worry about
        // login-view.fxml, once these branches are merged in the end it should work appropriately.

        // Load the login view when the user clicks the "Log Out" button
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);

    }



    @FXML
    protected void onScheduleButtonClick() {
//        Creates empty table with fields in database
        scheduleDAO.createTable();

//        Mock Data, Feel free to delete
        String dayOfWeek = "Tuesday";
        String eventName = "CAB302 Lecture";
        String eventDescription ="VIRTUAL TUTORIAL CAB302";
        String eventStartTime = "11 AM";
        String eventEndTime= "1 PM";
        scheduleDAO.insertSchedule(dayOfWeek, eventName, eventDescription, eventStartTime, eventEndTime);

//        With understanding that this button is for inserting the schedule, I attempted to
//        get the base table with just the days of the week generated to the active scene after
//        clicking the button. It isn't working though so feel free to remove it if It's incorrect.

//        This will take the longest time to make because we need to read the timetable file, insert
//        data from it into the JavaFX and then into the database



        Scene scene = new Scene(new Group());
        Stage stage = (Stage) scheduleButton.getScene().getWindow();

//        stage.setScene(scene);



//        Creating a new table for the days of the week in schedule
        TableView schedule = new TableView<>();

//        Creating table columns for days of the week in schedule
        TableColumn mondayCol = new TableColumn("Monday");
        TableColumn tuesdayCol = new TableColumn("Tuesday");
        TableColumn wednesdayCol = new TableColumn("Wednesday");
        TableColumn thursdayCol = new TableColumn("Thursday");
        TableColumn fridayCol = new TableColumn("Friday");
        TableColumn saturdayCol = new TableColumn("Saturday");
        TableColumn sundayCol = new TableColumn("Sunday");

//        Adding all the columns into the table
        schedule.getColumns().addAll(mondayCol,tuesdayCol,wednesdayCol,thursdayCol,fridayCol, saturdayCol,sundayCol);

        VBox insertTable  = new VBox();
        insertTable.getChildren().addAll(schedule);

        ((Group) scene.getRoot()).getChildren().addAll(insertTable);

        stage.setScene(scene);
        stage.show();
    }



//    These three methods will functionally be the same once we have figured out how to read from the timetable
//    and write to the database and to the JavaFX scene.

//    Methods for retrieving schedules based on the current week, next week or the previous week

    @FXML
    protected void onPreviousWeekButtonClick() {



    }

    @FXML
    protected void onCurrentWeekButtonClick() {

    }

    @FXML
    protected void onNextWeekButtonClick() {

    }

//    Methods for editing, updating and generating new schedules.

    @FXML
    protected void onChangeScheduleButtonClick() {

    }

    @FXML
    protected void onUpdateScheduleButtonClick() {

    }

    @FXML
    protected void onGenerateNewScheduleButtonClick() {
//        Deletes all data from existing schedule
        scheduleDAO.generateNewSchedule();
//        Runs scheduling button method to read and write new schedule file
        onScheduleButtonClick();

    }






}
