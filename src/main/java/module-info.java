module com.app.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires lombok;
    requires java.sql;
    requires mysql.connector.java;
    requires java.datatransfer;
    requires java.desktop;


    opens com.app.demo;
    exports com.app.demo.controller;
    exports com.app.demo.module;
    opens com.app.demo.controller to javafx.fxml, java.desktop;
    opens com.app.demo.module to javafx.base;
}
