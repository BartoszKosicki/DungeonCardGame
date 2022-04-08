package com.example.dungeaoncrawler;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class FightScene {

    public void startFight() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(MapScene.class.getResource("test.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Fight!");
            stage.setScene(scene);
            stage.show();
            stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::runFromFight);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runFromFight(WindowEvent event) {
        MapScene.player.takeDamage(15);
        MapController.canMove = true;
    }
}
