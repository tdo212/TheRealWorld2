module com.example.scheduling {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.scheduling to javafx.fxml;
    exports com.example.scheduling;
}