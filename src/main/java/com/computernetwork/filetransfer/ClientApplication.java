package com.computernetwork.filetransfer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ClientApplication extends Application {
    private MainController controller;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("File Transfer");
        stage.setScene(scene);
        stage.show();

        controller = fxmlLoader.getController();

    }
    @Override
    public void stop() {
        try {
            controller.onClose();
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}