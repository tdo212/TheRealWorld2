package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SqliteUserDAO;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class) // Initialize TestFX
public class RegisterControllerTest {

    private RegisterController registerController;
    private SqliteUserDAO mockUserDAO;

    @BeforeEach
    public void setUp() throws Exception {
        // Create an instance of the controller
        registerController = new RegisterController();

        // Create a mock of the SqliteUserDAO
        mockUserDAO = Mockito.mock(SqliteUserDAO.class);

        // Inject the mock DAO into the controller
        registerController.setUserDAO(mockUserDAO);

        // Manually initialize the JavaFX controls
        registerController.usernameField = new TextField();
        registerController.passwordField = new PasswordField();
        registerController.confirmPasswordField = new PasswordField();
        registerController.emailField = new TextField();
        registerController.phoneNumberField = new TextField();
    }

    @Test
    public void testValidRegistration() {
        // Set up the form with valid inputs
        registerController.usernameField.setText("testuser");
        registerController.passwordField.setText("password123");
        registerController.confirmPasswordField.setText("password123");
        registerController.emailField.setText("testuser@example.com");
        registerController.phoneNumberField.setText("1234567890");

        // Simulate the registration button click
        registerController.onRegisterButtonClick();

        // Verify that the addUser method was called once
        Mockito.verify(mockUserDAO, Mockito.times(1))
                .addUser("testuser", "password123", "testuser@example.com", "1234567890");
    }

    @Test
    public void testInvalidRegistrationPasswordMismatch() {
        // Set up the form with mismatched passwords
        registerController.usernameField.setText("testuser");
        registerController.passwordField.setText("password123");
        registerController.confirmPasswordField.setText("password456"); // Password mismatch
        registerController.emailField.setText("testuser@example.com");
        registerController.phoneNumberField.setText("1234567890");

        // Simulate the registration button click
        registerController.onRegisterButtonClick();

        // Verify no user was added
        Mockito.verify(mockUserDAO, Mockito.never())
                .addUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testRegistrationWithEmptyFields() {
        // Leave username and phone number empty
        registerController.usernameField.setText("");
        registerController.passwordField.setText("password123");
        registerController.confirmPasswordField.setText("password123");
        registerController.emailField.setText("testuser@example.com");
        registerController.phoneNumberField.setText("");

        // Simulate the registration button click
        registerController.onRegisterButtonClick();

        // Verify that addUser was never called due to missing fields
        Mockito.verify(mockUserDAO, Mockito.never())
                .addUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testAllFieldsEmpty() {
        // Leave all fields empty
        registerController.usernameField.setText("");
        registerController.passwordField.setText("");
        registerController.confirmPasswordField.setText("");
        registerController.emailField.setText("");
        registerController.phoneNumberField.setText("");

        // Simulate the registration button click
        registerController.onRegisterButtonClick();

        // Verify that addUser was never called due to all fields being empty
        Mockito.verify(mockUserDAO, Mockito.never())
                .addUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testUsernameFieldWithSpaces() {
        // Set up with leading/trailing spaces in the username
        registerController.usernameField.setText("  testuser  ");
        registerController.passwordField.setText("password123");
        registerController.confirmPasswordField.setText("password123");
        registerController.emailField.setText("testuser@example.com");
        registerController.phoneNumberField.setText("1234567890");

        // Simulate the registration button click
        registerController.onRegisterButtonClick();

        // Verify that addUser method was called with trimmed username
        Mockito.verify(mockUserDAO, Mockito.times(1))
                .addUser("testuser", "password123", "testuser@example.com", "1234567890");
    }



}
