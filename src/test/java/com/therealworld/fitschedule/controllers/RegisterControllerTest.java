package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SqliteUserDAO;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.api.FxToolkit;

@ExtendWith(ApplicationExtension.class) // Initialize TestFX
public class RegisterControllerTest {

    private RegisterController registerController;
    private SqliteUserDAO mockUserDAO;

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize the JavaFX toolkit
        FxToolkit.registerPrimaryStage();

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

        // Verify that the addUser method in the mock DAO was called once
        Mockito.verify(mockUserDAO, Mockito.times(1))
                .addUser("testuser", "password123", "testuser@example.com", "1234567890");
    }

    @Test
    public void testInvalidRegistrationPasswordMismatch() {
        // Set up the form with passwords that do not match
        registerController.usernameField.setText("testuser");
        registerController.passwordField.setText("password123");
        registerController.confirmPasswordField.setText("password456");
        registerController.emailField.setText("testuser@example.com");
        registerController.phoneNumberField.setText("1234567890");

        // Simulate the registration button click
        registerController.onRegisterButtonClick();

        // Verify that addUser was never called because of password mismatch
        Mockito.verify(mockUserDAO, Mockito.never())
                .addUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }
}
