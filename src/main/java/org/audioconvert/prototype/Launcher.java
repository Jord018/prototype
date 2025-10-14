package org.audioconvert.prototype;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.audioconvert.prototype.view.MainScreen;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AudioConverterUI.fxml"));
        Parent root = loader.load();

        MainScreen mainScreenController = loader.getController();
        mainScreenController.setPrimaryStage(primaryStage);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Audio Converter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
