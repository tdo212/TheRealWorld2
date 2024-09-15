package com.therealworld.fitschedule.controllers;


import com.therealworld.fitschedule.FitScheduleApp;

import com.therealworld.fitschedule.model.Contact;
import com.therealworld.fitschedule.model.IContactDAO;
import com.therealworld.fitschedule.model.MockContactDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class GoalsController {
    @FXML
    private ListView<Contact> contactsListView;
    private IContactDAO contactDAO;
    public GoalsController() {
        contactDAO = new MockContactDAO();
    }

    private ListCell<Contact> renderCell(ListView<Contact> contactListView) {
        return new ListCell<>() {

            /**
             * Updates the item in the cell by setting the text to the contact's full name.
             * @param contact The contact to update the cell with.
             * @param empty Whether the cell is empty.
             */
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                // If the cell is empty, set the text to null, otherwise set it to the contact's full name
                if (empty || contact == null || contact.getFullName() == null) {
                    setText(null);
                } else {
                    setText(contact.getFullName());
                }
            }
        };
    }


    /**
     * Synchronizes the contacts list view with the contacts in the database.
     */
    private void syncContacts() {
        contactsListView.getItems().clear();
        contactsListView.getItems().addAll(contactDAO.getAllContacts());
    }

    @FXML
    public void initialize() {
        contactsListView.setCellFactory(this::renderCell);
        syncContacts();
    }

}