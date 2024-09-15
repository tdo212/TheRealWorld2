package com.therealworld.fitschedule;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FitScheduleApp extends Application {

    // Constants defining the window title and size
    public static final String TITLE = "Goals";
    public static final int WIDTH = 960;
    public static final int HEIGHT = 540;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FitScheduleApp.class.getResource("/com/therealworld/fitschedule/NEW.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
