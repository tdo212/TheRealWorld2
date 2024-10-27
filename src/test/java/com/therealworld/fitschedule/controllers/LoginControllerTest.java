package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.model.SqliteDAO;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;

import static org.mockito.Mockito.*;

@Tag("gui")  // Tag added to exclude this test from CI/CD when specified
@ExtendWith(ApplicationExtension.class)
class LoginControllerTest {

    private LoginController loginController;  // Declare loginController at the class level
    private SqliteDAO mockUserDAO;

    @Start
    public void start(Stage stage) throws Exception {
        loginController = new LoginController();  // Instantiate loginController

        mockUserDAO = Mockito.mock(SqliteDAO.class);  // Create a mock of SqliteUserDAO
        loginController.userDAO = mockUserDAO;  // Inject mock into the controller

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
            Platform.runLater(() -> {
                try {
                    loginController.onLoginButtonClick();  // Ensure this method is triggered
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
        });
    }

    @Test
    public void testSuccessfulLogin() throws IOException {
        Platform.runLater(() -> {
            loginController.usernameField.setText("testuser");
            loginController.passwordField.setText("password123");
        });
        WaitForAsyncUtils.waitForFxEvents();  // Wait for UI events to complete

        when(mockUserDAO.authenticateUser("testuser", "password123")).thenReturn(true);
        Platform.runLater(() -> loginController.loginButton.fire());  // Simulate button click
        WaitForAsyncUtils.waitForFxEvents();  // Wait for UI events to complete

        verify(mockUserDAO, times(1)).authenticateUser("testuser", "password123");
    }

    @Test
    public void testFailedLogin() throws IOException {
        Platform.runLater(() -> {
            loginController.usernameField.setText("testuser");
            loginController.passwordField.setText("wrongpassword");
        });
        WaitForAsyncUtils.waitForFxEvents();  // Wait for UI events to complete

        when(mockUserDAO.authenticateUser("testuser", "wrongpassword")).thenReturn(false);
        Platform.runLater(() -> loginController.loginButton.fire());  // Simulate button click
        WaitForAsyncUtils.waitForFxEvents();  // Wait for UI events to complete

        verify(mockUserDAO, times(1)).authenticateUser("testuser", "wrongpassword");
    }

    @Test
    public void testEmptyUsernameAndPasswordFields() throws IOException {
        Platform.runLater(() -> {
            loginController.usernameField.setText("");
            loginController.passwordField.setText("");
        });
        WaitForAsyncUtils.waitForFxEvents();  // Wait for UI events to complete

        Platform.runLater(() -> loginController.loginButton.fire());  // Simulate button click
        WaitForAsyncUtils.waitForFxEvents();  // Wait for UI events to complete

        verify(mockUserDAO, never()).authenticateUser(anyString(), anyString());
    }

    @Test
    public void testOnlyUsernameFilled() throws IOException {
        Platform.runLater(() -> {
            loginController.usernameField.setText("testuser");
            loginController.passwordField.setText("");
        });
        WaitForAsyncUtils.waitForFxEvents();  // Wait for UI events to complete

        Platform.runLater(() -> loginController.loginButton.fire());  // Simulate button click
        WaitForAsyncUtils.waitForFxEvents();  // Wait for UI events to complete

        verify(mockUserDAO, never()).authenticateUser(anyString(), anyString());
    }

    @Test
    public void testOnlyPasswordFilled() throws IOException {
        Platform.runLater(() -> {
            loginController.usernameField.setText("");
            loginController.passwordField.setText("password123");
        });
        WaitForAsyncUtils.waitForFxEvents();  // Wait for UI events to complete

        Platform.runLater(() -> loginController.loginButton.fire());  // Simulate button click
        WaitForAsyncUtils.waitForFxEvents();  // Wait for UI events to complete

        verify(mockUserDAO, never()).authenticateUser(anyString(), anyString());
    }
}
