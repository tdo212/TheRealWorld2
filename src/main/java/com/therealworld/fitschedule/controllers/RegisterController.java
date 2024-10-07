package com.therealworld.fitschedule.controllers;

import com.therealworld.fitschedule.FitScheduleApp;
import com.therealworld.fitschedule.model.SqliteDAO;
import com.therealworld.fitschedule.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class responsible for handling user registration in the FitSchedule application.
 * Provides functionality to validate user inputs, register new users, and navigate between views.
 */
public class RegisterController {

    private SqliteDAO userDAO;

    private static final String REGISTRATION_SUCCESS = "User registered successfully.";
    private static final String REGISTRATION_FAILED = "Registration failed.";

    /**
     * Default constructor. Initializes the `SqliteDAO` instance for interacting with the user database.
     */
    public RegisterController() {
        this.userDAO = new SqliteDAO(); // Default behavior
    }

    /**
     * Setter method for injecting a mock DAO for testing purposes.
     *
     * @param userDAO the `SqliteDAO` instance to set.
     */
    public void setUserDAO(SqliteDAO userDAO) {
        this.userDAO = userDAO;
    }

    @FXML
    protected TextField usernameField;  // TextField for entering the username

    @FXML
    protected PasswordField passwordField;  // PasswordField for entering the password

    @FXML
    protected PasswordField confirmPasswordField;  // PasswordField for confirming the password

    @FXML
    protected TextField emailField;  // TextField for entering the email address

    @FXML
    protected TextField phoneNumberField;  // TextField for entering the phone number

    @FXML
    protected Button registerButton;  // Button to trigger the registration process

    @FXML
    protected Button backToLoginButton;  // Button to navigate back to the login view

    /**
     * Initializes the registration view components after the FXML elements are loaded.
     * Sets the default focus on the root element of the scene.
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> registerButton.getScene().getRoot().requestFocus());
    }

    /**
     * Handles the registration button click event.
     * Validates user inputs and attempts to register a new user.
     * If registration is successful, displays a success message; otherwise, shows an error message.
     */
    @FXML
    protected void onRegisterButtonClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();

        // Validate maximum username length (assuming the max length is 25 characters)
        if (username.length() > 25) {
            showAlert("Username exceeds maximum length of 25 characters", Alert.AlertType.ERROR);
            return;  // Stop further processing
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showAlert("Invalid email format", Alert.AlertType.ERROR);
            return;  // Stop further processing
        }

        // Validate phone number format
        if (!isValidPhoneNumber(phoneNumber)) {
            showAlert("Invalid phone number format", Alert.AlertType.ERROR);
            return;  // Stop further processing
        }

        // Collect empty fields
        List<String> emptyFields = getEmptyFields(username, password, confirmPassword, email, phoneNumber);

        if (!emptyFields.isEmpty()) {
            showAlert("The following fields are empty: " + String.join(", ", emptyFields), Alert.AlertType.ERROR);
        } else if (!password.equals(confirmPassword)) {
            showAlert("Passwords do not match", Alert.AlertType.ERROR);
        } else {
            // Proceed with registration if validation passes
            userDAO.addUser(username, password, email, phoneNumber);
            showAlert(REGISTRATION_SUCCESS, Alert.AlertType.INFORMATION);
        }
    }

    /**
     * Retrieves a list of empty fields based on user inputs.
     *
     * @param username       the username entered by the user.
     * @param password       the password entered by the user.
     * @param confirmPassword the confirmed password entered by the user.
     * @param email          the email entered by the user.
     * @param phoneNumber    the phone number entered by the user.
     * @return a list of empty field names as strings.
     */
    private List<String> getEmptyFields(String username, String password, String confirmPassword, String email, String phoneNumber) {
        List<String> emptyFields = new ArrayList<>();

        if (username.isEmpty()) emptyFields.add("Username");
        if (password.isEmpty()) emptyFields.add("Password");
        if (confirmPassword.isEmpty()) emptyFields.add("Confirm Password");
        if (email.isEmpty()) emptyFields.add("Email");
        if (phoneNumber.isEmpty()) emptyFields.add("Phone Number");

        return emptyFields;
    }

    /**
     * Displays an alert dialog with the specified message and alert type.
     * Used to show error messages or notifications to the user.
     *
     * @param message the message to be displayed in the alert.
     * @param type    the type of alert (e.g., information, error).
     */
    private void showAlert(String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Handles the show users button click event.
     * Retrieves and displays all users in the console.
     */
    @FXML
    protected void onShowUsersButtonClick() {
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Phone Number: " + user.getPhoneNumber());
            System.out.println("----");
        }
    }

    /**
     * Handles the back to login button click event.
     * Navigates the user back to the login view.
     *
     * @param event the event triggered by the button click.
     * @throws IOException if there is an error loading the login view FXML file.
     */
    @FXML
    protected void onBackToLoginClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), FitScheduleApp.WIDTH, FitScheduleApp.HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Clears the focus from all text fields and buttons in the registration form.
     */
    @FXML
    protected void clearFocus() {
        Platform.runLater(() -> registerButton.getScene().getRoot().requestFocus());
    }

    /**
     * Validates the format of the provided email.
     *
     * @param email the email string to validate.
     * @return `true` if the email format is valid, otherwise `false`.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; // Basic regex for email validation
        return email.matches(emailRegex);
    }

    /**
     * Validates the format of the provided phone number.
     *
     * @param phoneNumber the phone number string to validate.
     * @return `true` if the phone number format is valid, otherwise `false`.
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^[+]?[0-9]{10,15}$"; // Allows optional '+' and between 10 to 15 digits
        return phoneNumber.matches(phoneRegex);
    }
}
