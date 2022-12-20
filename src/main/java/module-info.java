module com.laula.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;


    opens com.laula.demo to javafx.fxml;
    exports com.laula.demo.controller;
    opens com.laula.demo.controller to javafx.fxml;
    exports com.laula.demo.module;
}