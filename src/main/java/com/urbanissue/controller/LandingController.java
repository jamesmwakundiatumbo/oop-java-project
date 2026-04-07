package com.urbanissue.controller;

import com.urbanissue.Main;
import com.urbanissue.util.UserGuideHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * CivicTrack welcome screen: residents and MCAs/ward admins + Learn more (classpath text).
 */
public class LandingController {

    private static final String LEARN_CITIZENS = "/com/urbanissue/text/learn-more-citizens.txt";
    private static final String LEARN_MCAS_WARD_ADMINS = "/com/urbanissue/text/learn-more-mcas-ward-admins.txt";

    @FXML private Label footerTaglineLabel;

    @FXML
    public void initialize() {
        footerTaglineLabel.setText(
                "CivicTrack — Mapping and reporting street problems to the teams responsible for fixing them."
        );
    }

    @FXML
    private void handleLearnMoreCitizens() {
        UserGuideHelper.showTextFromResource(
                stage(),
                "For residents — Learn more",
                LEARN_CITIZENS
        );
    }

    @FXML
    private void handleLearnMoreMcasWardAdmins() {
        UserGuideHelper.showTextFromResource(
                stage(),
                "For MCAs & ward admins — Learn more",
                LEARN_MCAS_WARD_ADMINS
        );
    }

    @FXML
    private void handleSignIn() throws IOException {
        Stage stage = stage();
        double w = stage.getWidth();
        double h = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();
        boolean max = stage.isMaximized();

        Parent root = FXMLLoader.load(Main.class.getResource("/com/urbanissue/fxml/Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/com/urbanissue/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("CivicTrack - Sign in");
        if (max) {
            stage.setMaximized(true);
        } else {
            stage.setWidth(w);
            stage.setHeight(h);
            stage.setX(x);
            stage.setY(y);
        }
    }

    private Stage stage() {
        return (Stage) footerTaglineLabel.getScene().getWindow();
    }
}
