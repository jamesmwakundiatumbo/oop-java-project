package com.urbanissue.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Handles file selection and copy for issue attachments. Saves to a local uploads folder.
 */
public final class FileUploadHelper {

    private static final String UPLOAD_DIR = "uploads";

    private FileUploadHelper() {}

    public static String chooseAndSaveFile(Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select image or document");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        var file = chooser.showOpenDialog(owner);
        if (file == null) return null;
        try {
            Path dir = Path.of(UPLOAD_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            Path target = dir.resolve(file.getName());
            Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException e) {
            AlertHelper.showError("Upload failed", "Could not save file: " + e.getMessage());
            return null;
        }
    }
}
