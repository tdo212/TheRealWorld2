package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SqliteUserDAO;
import javafx.application.Platform;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.anyString;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.CountDownLatch;

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
    @Test
    public void testUsernameExceedsMaxLength() {
        registerController.usernameField.setText("a".repeat(26)); // Assuming max length is 255
        registerController.passwordField.setText("password123");
        registerController.confirmPasswordField.setText("password123");
        registerController.emailField.setText("testuser@example.com");
        registerController.phoneNumberField.setText("1234567890");

        // Simulate the registration button click
        registerController.onRegisterButtonClick();

        // Verify that userDAO.addUser() was NEVER called due to excessive username length
        Mockito.verify(mockUserDAO, Mockito.never()).addUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testInvalidEmailFormat() {
        registerController.usernameField.setText("testuser");
        registerController.passwordField.setText("password123");
        registerController.confirmPasswordField.setText("password123");
        registerController.emailField.setText("invalid-email"); // Invalid email format
        registerController.phoneNumberField.setText("1234567890");

        // Simulate registration click
        registerController.onRegisterButtonClick();

        // Verify that user is not added due to invalid email
        Mockito.verify(mockUserDAO, Mockito.never()).addUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testInvalidPhoneNumber() {
        registerController.usernameField.setText("testuser");
        registerController.passwordField.setText("password123");
        registerController.confirmPasswordField.setText("password123");
        registerController.emailField.setText("testuser@example.com");
        registerController.phoneNumberField.setText("invalid-phone"); // Invalid phone number format

        // Simulate registration click
        registerController.onRegisterButtonClick();

        // Verify that user is not added due to invalid phone number
        Mockito.verify(mockUserDAO, Mockito.never()).addUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testValidPhoneNumberWithPlus() {
        registerController.usernameField.setText("testuser");
        registerController.passwordField.setText("password123");
        registerController.confirmPasswordField.setText("password123");
        registerController.emailField.setText("testuser@example.com");
        registerController.phoneNumberField.setText("+123456789012"); // Valid international phone number

        // Simulate registration click
        registerController.onRegisterButtonClick();

        // Verify that the user is added
        Mockito.verify(mockUserDAO, Mockito.times(1)).addUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testShortPhoneNumber() {
        registerController.usernameField.setText("testuser");
        registerController.passwordField.setText("password123");
        registerController.confirmPasswordField.setText("password123");
        registerController.emailField.setText("testuser@example.com");
        registerController.phoneNumberField.setText("123"); // Too short phone number

        // Simulate registration click
        registerController.onRegisterButtonClick();

        // Verify that user is not added due to invalid phone number
        Mockito.verify(mockUserDAO, Mockito.never()).addUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testSimultaneousRegistrations() throws InterruptedException {
        int threadCount = 5; // Number of simultaneous registrations
        CountDownLatch startLatch = new CountDownLatch(1); // Latch to ensure all threads start at the same time
        CountDownLatch doneLatch = new CountDownLatch(threadCount); // Latch to wait for all threads to finish

        Runnable registrationTask = () -> {
            try {
                startLatch.await(); // All threads wait here until latch is released
                Platform.runLater(() -> {
                    // Set unique values for each thread
                    registerController.usernameField.setText("user" + Thread.currentThread().getId());
                    registerController.passwordField.setText("password123");
                    registerController.confirmPasswordField.setText("password123");
                    registerController.emailField.setText("testuser" + Thread.currentThread().getId() + "@example.com");
                    registerController.phoneNumberField.setText("1234567890");

                    // Simulate the registration button click
                    registerController.onRegisterButtonClick();
                    doneLatch.countDown(); // Mark this thread as done
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Create and start multiple threads to simulate simultaneous registration attempts
        for (int i = 0; i < threadCount; i++) {
            new Thread(registrationTask).start();
        }

        startLatch.countDown(); // Release all threads to start at the same time
        doneLatch.await(); // Wait for all threads to finish

        // Verify all users were registered
        Mockito.verify(mockUserDAO, Mockito.times(threadCount))
                .addUser(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }









}