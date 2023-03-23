package com.app.demo.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(Main.class.getResource("/com/app/demo/product-view.fxml"));
                // Cargo la ventana
                AnchorPane window = fxmlLoader.load();

                primaryStage.setTitle("App Model");
                URL resource = getClass().getResource("/com/app/demo/images/Ignacio-Ibañez.png");
                Image image = new Image(resource.toString());
                primaryStage.getIcons().add(image);

                // Cargo el scene
                Scene scene = new Scene(window);

                // Setteo la scene y la muestro
                scene.setFill(Color.TRANSPARENT);
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.getLogger(ex.getMessage());
            }
        }

        public static void main (String[]args){
            launch(args);
        }
    }