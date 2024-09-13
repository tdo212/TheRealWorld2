package com.therealworld.fitschedule.controllers;


import com.therealworld.fitschedule.FitScheduleApp;

import com.therealworld.fitschedule.model.Contact;
import com.therealworld.fitschedule.model.IContactDAO;
import com.therealworld.fitschedule.model.MockContactDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class GoalsController {
    @FXML
    private IContactDAO contactDAO;
    public GoalsController() {
        contactDAO = new MockContactDAO();
    }



}