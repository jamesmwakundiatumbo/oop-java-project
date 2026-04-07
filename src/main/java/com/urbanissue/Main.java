package com.urbanissue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * CivicTrack - Urban Issue Reporting System. Entry point.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        URL fxml = getClass().getResource("/com/urbanissue/fxml/Landing.fxml");
        if (fxml == null) {
            throw new IOException("Landing.fxml not found. Ensure resources are on classpath.");
        }
        Parent root = FXMLLoader.load(fxml);
        double vw = Screen.getPrimary().getVisualBounds().getWidth();
        double vh = Screen.getPrimary().getVisualBounds().getHeight();
        double w = Math.min(1280, Math.max(960, vw * 0.92));
        double h = Math.min(900, Math.max(640, vh * 0.88));
        Scene scene = new Scene(root, w, h);
        scene.getStylesheets().add(getClass().getResource("/com/urbanissue/css/app.css").toExternalForm());
        stage.setTitle("CivicTrack - Welcome");
        stage.setMinWidth(880);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
