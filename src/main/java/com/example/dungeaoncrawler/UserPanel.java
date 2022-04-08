package com.example.dungeaoncrawler;

import com.example.dungeaoncrawler.logic.actors.player.MageClass;
import com.example.dungeaoncrawler.logic.actors.player.Player;
import com.example.dungeaoncrawler.logic.actors.player.WarriorClass;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserPanel extends Application implements Initializable {

    MapScene mapScene;
    ObservableList<String> charactersList = FXCollections.observableArrayList("Mag", "Warrior");
    public static String userName;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerSelector.setValue("Mag");
        playerSelector.getItems().addAll(charactersList);
    }


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MapScene.class.getResource("user-panel.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CardCrawl");
        stage.setScene(scene);
        stage.show();
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::endGame);
    }

    @FXML
    void startGame() {
        Player player = null;
        int playerType = playerSelector.getSelectionModel().getSelectedIndex();
        if (Objects.equals(playerType, 0)) {
            player = new MageClass(50, 30, 0, getIdUserName(), 4, null);
        } else if (Objects.equals(playerType, 1)) {
            player = new WarriorClass(100, 0, 20, getIdUserName(), 3, null);
        }
        mapScene = new MapScene(player);
        mapScene.loadNewGame();
    }

    @FXML
    private TextField idUserName;

    @FXML
    void loadPreviousGame(ActionEvent event) {
        mapScene.loadSavedGame();
    }

    @FXML
    void endGame(WindowEvent event) {
        exit();
    }

    @FXML
    void exitGame(ActionEvent event) {
        exit();
    }

    void exit() {
        Platform.exit();
        System.exit(0);
    }

    public String getIdUserName() {
        return userName = idUserName.getText();
    }

    @FXML
    private Button startButton;

    @FXML
    private ChoiceBox<String> playerSelector;
}
