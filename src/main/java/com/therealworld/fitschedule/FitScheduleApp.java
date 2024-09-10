package com.therealworld.fitschedule;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class FitScheduleApp extends Application {

    // Constants defining the window title and size
    public static final String TITLE = "Login";
    public static final int WIDTH = 640;
    public static final int HEIGHT = 360;

    @Override
    public void start(Stage stage) throws IOException {
        // Log the path to the logo image for debugging purposes
        System.out.println(FitScheduleApp.class.getResource("/com/therealworld/fitschedule/images/logo.png"));

        // Fetch the FXML file URL
        URL fxmlFileUrl = FitScheduleApp.class.getResource("/com/therealworld/fitschedule/login-view.fxml");

        // Print the path to the resource for debugging purposes
        System.out.println("FXML URL: " + fxmlFileUrl);

        // Check if the resource is loaded correctly
        if (fxmlFileUrl == null) {
            System.out.println("FXML file not found!");
            return; // Stop the application if the FXML is missing
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlFileUrl);

        // Load the scene and set it
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
