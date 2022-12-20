package com.laula.demo.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader( );
            fxmlLoader.setLocation(Main.class.getResource("/com/laula/demo/product-view.fxml"));
            // Cargo la ventana
            Pane window = fxmlLoader.load();

            // Cargo el scene
            Scene scene = new Scene(window);

            // Setteo la scene y la muestro
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.getLogger(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}