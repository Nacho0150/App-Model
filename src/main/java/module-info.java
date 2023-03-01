module com.laula.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires lombok;
    requires java.sql;
    requires mysql.connector.java;
    requires java.datatransfer;
    requires java.desktop;


    opens com.laula.demo;
    exports com.laula.demo.controller;
    exports com.laula.demo.module;
    opens com.laula.demo.controller to javafx.fxml, java.desktop;
    opens com.laula.demo.module to javafx.base;
}
