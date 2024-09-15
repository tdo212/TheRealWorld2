package com.therealworld.fitschedule.model;


import java.util.ArrayList;
import java.util.List;

public class MockContactDAO implements IContactDAO {
    /**
     * A static list of contacts to be used as a mock database.
     */
    public static final ArrayList<Contact> contacts = new ArrayList<>();
    private static int autoIncrementedId = 0;

    public MockContactDAO() {
        // Add some initial contacts to the mock database
        addContact(new Contact("Username: ", "tdo212", "4/10/2024", "0423423425"));
        addContact(new Contact("Streak: 50 points", "", "12/9/2024", "0423423423"));
        addContact(new Contact("Hours Exercised: 39", "", "19/9/2024", "0423423424"));
        addContact(new Contact("Days Exercised: 10", "", "26/9/2024", "0423423425"));
        addContact(new Contact("Goals Completed: 22", "", "4/10/2024", "0423423425"));

    }

    @Override
    public void addContact(Contact contact) {
        contact.setId(autoIncrementedId);
        autoIncrementedId++;
        contacts.add(contact);
    }

    @Override
    public void updateContact(Contact contact) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId() == contact.getId()) {
                contacts.set(i, contact);
                break;
            }
        }
    }

    @Override
    public void deleteContact(Contact contact) {
        contacts.remove(contact);
    }

    @Override
    public Contact getContact(int id) {
        for (Contact contact : contacts) {
            if (contact.getId() == id) {
                return contact;
            }
        }
        return null;
    }

    @Override
    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }
}