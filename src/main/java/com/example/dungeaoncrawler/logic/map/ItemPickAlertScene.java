package com.example.dungeaoncrawler.logic.map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class ItemPickAlertScene {

    public static void displayAlertBox(String title, String message, String imageName, int height) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(450);
        window.setMinHeight(150);

        Label label = new Label();

        Image img = new Image(imageName);
        ImageView view = new ImageView(img);
        view.setFitHeight(height);
        view.setPreserveRatio(true);
        label.setGraphic(view);
        label.setText(message);

        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
