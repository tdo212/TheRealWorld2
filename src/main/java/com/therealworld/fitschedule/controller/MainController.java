package main.java.com.example.addressbook.controller;

import com.example.addressbook.model.Contact;
import com.example.addressbook.model.IContactDAO;
import com.example.addressbook.model.MockContactDAO;
import com.example.addressbook.model.SqliteContactDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.List;


public class MainController {
    @FXML
    private ListView<Contact> contactsListView;
    private IContactDAO contactDAO;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private VBox contactContainer;
    public MainController() {
        contactDAO = new SqliteContactDAO();
    }
    /**
     * Programmatically selects a contact in the list view and
     * updates the text fields with the contact's information.
     * @param contact The contact to select.
     */
    private void selectContact(Contact contact) {
        contactsListView.getSelectionModel().select(contact);
        firstNameTextField.setText(contact.getFirstName());
        lastNameTextField.setText(contact.getLastName());
        emailTextField.setText(contact.getEmail());
        phoneTextField.setText(contact.getPhone());
    }
    /**
     * Renders a cell in the contacts list view by setting the text to the contact's full name.
     * @param contactListView The list view to render the cell for.
     * @return The rendered cell.
     */
    private ListCell<Contact> renderCell(ListView<Contact> contactListView) {
        return new ListCell<>() {
            /**
             * Handles the event when a contact is selected in the list view.
             * @param mouseEvent The event to handle.
             */
            private void onContactSelected(MouseEvent mouseEvent) {
                ListCell<Contact> clickedCell = (ListCell<Contact>) mouseEvent.getSource();
                // Get the selected contact from the list view
                Contact selectedContact = clickedCell.getItem();
                if (selectedContact != null) selectContact(selectedContact);
            }

            /**
             * Updates the item in the cell by setting the text to the contact's full name.
             * @param contact The contact to update the cell with.
             * @param empty Whether the cell is empty.
             */
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                // If the cell is empty, set the text to null, otherwise set it to the contact's full name
                if (empty || contact == null || contact.getContactSummary() == null) {
                    setText(null);
                    super.setOnMouseClicked(this::onContactSelected);
                } else {
                    setText(contact.getContactSummary());
                }
            }
        };
    }

    /**
     * Synchronizes the contacts list view with the contacts in the database.
     */
    private void syncContacts() {
        // Save the currently selected contact
        Contact currentContact = contactsListView.getSelectionModel().getSelectedItem();

        // Clear the current list
        contactsListView.getItems().clear();

        // Retrieve all contacts from the DAO
        List<Contact> contacts = contactDAO.getAllContacts();

        // Debugging: Print each contact's summary to the console
        for (Contact contact : contacts) {
            System.out.println(contact.getContactSummary());  // Print the contact summary
        }

        boolean hasContact = !contacts.isEmpty();

        if (hasContact) {
            // Add all contacts to the ListView
            contactsListView.getItems().addAll(contacts);

            // Determine which contact to select next
            Contact nextContact = contacts.contains(currentContact) ? currentContact : contacts.get(0);

            // Select the appropriate contact
            contactsListView.getSelectionModel().select(nextContact);

            // Perform any additional selection logic
            selectContact(nextContact);
        }

        // Show/hide the contact container based on whether there are contacts
        contactContainer.setVisible(hasContact);
    }



    @FXML
    public void initialize() {
        contactsListView.setCellFactory(this::renderCell);
        syncContacts();
        // Select the first contact and display its information
        contactsListView.getSelectionModel().selectFirst();
        Contact firstContact = contactsListView.getSelectionModel().getSelectedItem();
        if (firstContact != null) {
            selectContact(firstContact);
        }
    }
    @FXML
    private void onDelete() {
        // Get the selected contact from the list view
        Contact selectedContact = contactsListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            contactDAO.deleteContact(selectedContact);
            syncContacts();
        }
    }

    @FXML
    private void onEditConfirm() {
        // Get the selected contact from the list view
        Contact selectedContact = contactsListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            selectedContact.setFirstName(firstNameTextField.getText());
            selectedContact.setLastName(lastNameTextField.getText());
            selectedContact.setEmail(emailTextField.getText());
            selectedContact.setPhone(phoneTextField.getText());
            contactDAO.updateContact(selectedContact);
            syncContacts();
        }
    }
    @FXML
    private void onAdd() {
        // Default values for a new contact
        final String DEFAULT_FIRST_NAME = "New";
        final String DEFAULT_LAST_NAME = "Contact";
        final String DEFAULT_EMAIL = "";
        final String DEFAULT_PHONE = "";

        // Create a new contact object
        Contact newContact = new Contact(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_EMAIL, DEFAULT_PHONE);

        // Add the new contact to the database
        contactDAO.addContact(newContact);

        // Refresh the list view and select the new contact
        syncContacts();  // Refresh the ListView with the updated contacts
        contactsListView.getSelectionModel().select(newContact);  // Select the newly added contact
        firstNameTextField.requestFocus();  // Focus on the first name text field for editing
    }


    @FXML
    private void onCancel() {
        // Find the selected contact
        Contact selectedContact = contactsListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            // Since the contact hasn't been modified,
            // we can just re-select it to refresh the text fields
            selectContact(selectedContact);
        }
    }
}