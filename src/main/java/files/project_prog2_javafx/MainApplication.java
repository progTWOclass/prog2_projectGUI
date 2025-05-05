// -------------------------------------------------------
// Final Project
// Written by: Steve Banh 1971537
// For “Programming 2” Section 02 – Winter 2025
// --------------------------------------------------------
/**
 * Our main class to run our personal finance tracker application
 * */
package files.project_prog2_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader mainScreen = new FXMLLoader(MainApplication.class.getResource("project-view.fxml"));
        stage.setTitle("Personal Finance Tracker");
        stage.setScene(new Scene(mainScreen.load()));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}