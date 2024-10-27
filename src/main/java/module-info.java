module com.therealworld.fitschedule {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires java.desktop;

    opens com.therealworld.fitschedule to javafx.fxml;
    exports com.therealworld.fitschedule;
    exports com.therealworld.fitschedule.controllers;
    opens com.therealworld.fitschedule.controllers to javafx.fxml;
    exports com.therealworld.fitschedule.model;
    opens com.therealworld.fitschedule.model to javafx.fxml;

}
