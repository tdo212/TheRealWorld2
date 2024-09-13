package com.therealworld.fitschedule.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FitScheduleDBConnection {
    private static Connection instance = null;

    private FitScheduleDBConnection () {
        String url = "jdbc:sqlite:schedulingdatabase.db";
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new FitScheduleDBConnection ();
        }
        return instance;
    }
}