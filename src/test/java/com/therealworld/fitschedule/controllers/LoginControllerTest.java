package com.therealworld.fitschedule.controllers;


import com.therealworld.fitschedule.model.SqliteDAO;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class LoginControllerTest {

    private LoginController loginController;  // Declare loginController at the class level
    private SqliteDAO mockUserDAO;

    @Start
    public void start(Stage stage) throws Exception {
        // Initialize the LoginController
        loginController = new LoginController();  // Instantiate loginController

        // Create a mock of the SqliteUserDAO
        mockUserDAO = Mockito.mock(SqliteDAO.class);

        // Inject mock into the controller
        loginController.userDAO = mockUserDAO;

        // Manually initialize the JavaFX controls
        loginController.usernameField = new TextField();
        loginController.passwordField = new PasswordField();
        loginController.loginButton = new Button("Login");

        // Set up the scene with all necessary fields
        Scene scene = new Scene(new javafx.scene.layout.VBox(loginController.usernameField, loginController.passwordField, loginController.loginButton), 400, 300);
        stage.setScene(scene);
        stage.show();

        // Attach the button to trigger the login click
        loginController.loginButton.setOnAction(e -> {
            try {
                loginController.onLoginButtonClick();  // Ensure this method is triggered
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    @Test
    public void testSuccessfulLogin() throws IOException {
        // Arrange
        loginController.usernameField.setText("testuser");
        loginController.passwordField.setText("password123");

        // Mock the authenticateUser method to return true for successful login
        when(mockUserDAO.authenticateUser("testuser", "password123")).thenReturn(true);

        // Act
        loginController.loginButton.fire();  // Simulate button click

        // Assert
        verify(mockUserDAO, times(1)).authenticateUser("testuser", "password123");

        // Additional assertions can be added to verify scene change or alerts
    }

    @Test
    public void testFailedLogin() throws IOException {
        // Arrange
        loginController.usernameField.setText("testuser");
        loginController.passwordField.setText("wrongpassword");

        // Mock the authenticateUser method to return false for failed login
        when(mockUserDAO.authenticateUser("testuser", "wrongpassword")).thenReturn(false);

        // Act
        loginController.loginButton.fire();  // Simulate button click

        // Assert
        verify(mockUserDAO, times(1)).authenticateUser("testuser", "wrongpassword");
    }

    @Test
    public void testEmptyUsernameAndPasswordFields() throws IOException {
        // Arrange: Set both fields to empty strings
        loginController.usernameField.setText("");
        loginController.passwordField.setText("");

        // Act: Simulate the login button click
        loginController.loginButton.fire();

        // Assert: Verify that authenticateUser() was never called
        verify(mockUserDAO, never()).authenticateUser(anyString(), anyString());

        // Optionally, you can also verify that an alert was shown for empty fields
    }

    @Test
    public void testOnlyUsernameFilled() throws IOException {
        // Arrange
        loginController.usernameField.setText("testuser");
        loginController.passwordField.setText("");

        // Act
        loginController.loginButton.fire();

        // Assert
        verify(mockUserDAO, never()).authenticateUser(anyString(), anyString());
    }

    @Test
    public void testOnlyPasswordFilled() throws IOException {
        // Arrange
        loginController.usernameField.setText("");
        loginController.passwordField.setText("password123");

        // Act
        loginController.loginButton.fire();

        // Assert
        verify(mockUserDAO, never()).authenticateUser(anyString(), anyString());
    }
}