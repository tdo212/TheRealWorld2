package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.DatabaseHelper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class YourController {

    @FXML
    private ListView<String> contactsListView;

    public void initialize() {
        ObservableList<String> data = DatabaseHelper.getAllGoals();
        System.out.println("Number of items to display: " + data.size());
        contactsListView.setItems(data);
    }
}