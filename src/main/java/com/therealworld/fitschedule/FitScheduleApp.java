package com.therealworld.fitschedule;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * The main entry point for the FitSchedule application.
 * This class extends `javafx.application.Application` and sets up the primary stage and scene for the application.
 * It initializes the application window and loads the initial login view.
 */
public class FitScheduleApp extends Application {

    /**
     * The title of the application window.
     */
    public static final String TITLE = "Scheduling";

    /**
     * The width of the application window in pixels.
     */
    public static final int WIDTH = 900;

    /**
     * The height of the application window in pixels.
     */
    public static final int HEIGHT = 600;

    /**
     * The main entry point for the JavaFX application.
     * This method is called when the application is launched and is responsible for setting up the primary stage.
     *
     * @param stage the primary stage for this application, onto which the application scene can be set.
     * @throws IOException if there is an error loading the FXML file for the initial scene.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("/com/therealworld/fitschedule/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);  // Set the title of the window
        stage.setScene(scene);  // Set the scene onto the stage
        stage.show();  // Display the stage
    }

    /**
     * The main method, which serves as the entry point for launching the application.
     * It calls `launch()` to start the JavaFX application.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch();  // Launch the JavaFX application
    }
}
