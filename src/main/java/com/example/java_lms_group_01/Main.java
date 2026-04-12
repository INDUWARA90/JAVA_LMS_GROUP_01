package com.example.java_lms_group_01;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Application entry point.
 * JavaFX starts here and opens the login screen first.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the first screen shown to the user.
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/login_page.fxml")
        );
        Scene scene = new Scene(loader.load());
        stage.setTitle("Login Page");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Hand control over to JavaFX.
        launch();
    }
}
