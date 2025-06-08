package com.metait.open2samepdf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Open2SamePdfApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Open2SamePdfApplication.class.getResource("Open2SamePdf-view.fxml"));
        Open2SamePdfController controller = new Open2SamePdfController();
        controller.setStage(stage);
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 720, 240);
        stage.setTitle("Open2SamePdf");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}