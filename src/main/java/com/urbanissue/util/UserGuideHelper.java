package com.urbanissue.util;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Shows scrollable help text loaded from the classpath (works in JAR and IDE — no external file paths).
 */
public final class UserGuideHelper {

    private static final String USER_GUIDE_RESOURCE = "/com/urbanissue/text/user-guide.txt";

    private UserGuideHelper() {}

    /** Full app user guide (features walkthrough). */
    public static void show(Stage owner) {
        showTextFromResource(owner, "CivicTrack — User guide", USER_GUIDE_RESOURCE);
    }

    /**
     * Opens a modal window with text loaded from a classpath resource.
     * {@code resourcePath} must start with "/" e.g. "/com/urbanissue/text/learn-more-citizens.txt"
     */
    public static void showTextFromResource(Stage owner, String windowTitle, String resourcePath) {
        String text = loadText(resourcePath);
        Stage dialog = new Stage();
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle(windowTitle);

        TextArea area = new TextArea(text);
        area.setEditable(false);
        area.setWrapText(true);
        area.setStyle("-fx-font-family: 'Segoe UI', 'SansSerif'; -fx-font-size: 13px;");

        ScrollPane scroll = new ScrollPane(area);
        scroll.setFitToWidth(true);
        scroll.setPadding(new Insets(8));

        Button close = new Button("Close");
        close.setDefaultButton(true);
        close.setOnAction(e -> dialog.close());

        HBox bottom = new HBox(close);
        bottom.setPadding(new Insets(0, 12, 12, 12));

        BorderPane root = new BorderPane();
        root.setCenter(scroll);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 760, 560);
        dialog.setScene(scene);
        dialog.setMinWidth(520);
        dialog.setMinHeight(400);
        dialog.show();
    }

    private static String loadText(String resourcePath) {
        String path = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        try (InputStream in = UserGuideHelper.class.getResourceAsStream(path)) {
            if (in == null) {
                return "Content not found in app bundle: " + path + "\n\n"
                        + "Rebuild the project (mvn compile) so resources are copied to target/classes.";
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Could not load content: " + e.getMessage();
        }
    }
}
