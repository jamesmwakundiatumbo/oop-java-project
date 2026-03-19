package com.urbanissue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * CivicTrack - Urban Issue Reporting System. Entry point.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        URL fxml = getClass().getResource("/com/urbanissue/fxml/Login.fxml");
        if (fxml == null) {
            throw new IOException("Login.fxml not found. Ensure resources are on classpath.");
        }
        Parent root = FXMLLoader.load(fxml);
        Scene scene = new Scene(root, 420, 380);
        scene.getStylesheets().add(getClass().getResource("/com/urbanissue/css/global.css") != null
                ? getClass().getResource("/com/urbanissue/css/global.css").toExternalForm() : "");
        stage.setTitle("CivicTrack - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
