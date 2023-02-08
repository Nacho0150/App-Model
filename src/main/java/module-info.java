module com.laula.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires lombok;
    requires java.sql;
    requires mysql.connector.java;


    opens com.laula.demo;
    exports com.laula.demo.controller;
    opens com.laula.demo.controller to javafx.fxml;
    opens com.laula.demo.module to javafx.base;
}
