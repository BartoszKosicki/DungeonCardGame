package com.example.dungeaoncrawler;

import com.example.dungeaoncrawler.logic.map.*;
import com.example.dungeaoncrawler.logic.actors.Actor;
import com.example.dungeaoncrawler.logic.actors.enemy.Enemy;
import com.example.dungeaoncrawler.logic.actors.player.Player;
import com.example.dungeaoncrawler.logic.items.cards.CardRarity;
import com.example.dungeaoncrawler.logic.items.cards.Cards;
import com.example.dungeaoncrawler.logic.items.cards.CardsType;
import com.example.dungeaoncrawler.utils.ImageHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.Objects;
import java.util.Random;

import static com.example.dungeaoncrawler.MapScene.player;
import static com.example.dungeaoncrawler.MapScene.worldMap;

public class MapController {

    public static boolean canMove = true;
    public static Enemy opponent;
    public static boolean isPlayerAlive = true;
    private final Image tileset = new Image("mapObjects.png", 577 * 2, 577 * 2, true, false);
    Thread independentEnemiesMoves = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getEnemyMove();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        printMap();
                    }
                });
            }
        }
    });

    @FXML
    private GridPane baseMap;

    @FXML
    private GridPane gridMap;

    @FXML
    private GridPane actorMap;

    @FXML
    private TableView<PlayerStatisticsOnMap> TableView;

    @FXML
    private TableColumn<PlayerStatisticsOnMap, String> TableStatisticName;

    @FXML
    private TableColumn<PlayerStatisticsOnMap, Integer> TableStatisticPoints;

    @FXML
    private VBox playerCardDeck;

    @FXML
    private TextField deckPlayerName;

    public void closeWindow() {
        System.out.println("close window");
        independentEnemiesMoves.stop();
    }

    public void initialize() {
        deckPlayerName.setText(player.getName() + "'s deck");
        canMove = true;
        printMap();
        printMinimap();
        updateDeck();
        independentEnemiesMoves.setDaemon(true);
        independentEnemiesMoves.start();
        loadStatistics();
    }

    public void printMap() {
        gridMap.getChildren().clear();
        baseMap.getChildren().clear();
        actorMap.getChildren().clear();
        loadStatistics();

        GameMap map = worldMap.getGameMap(worldMap.getCurrentPos()[0], worldMap.getCurrentPos()[1]);
        gridMap.setHgap(0);
        gridMap.setVgap(0);

        baseMap.setHgap(0);
        baseMap.setVgap(0);

        actorMap.setHgap(0);
        actorMap.setVgap(0);

        for (int i = 0; i < gridMap.getColumnCount(); i++) {
            for (int j = 0; j < gridMap.getRowCount(); j++) {

                Cell currCell = map.getCell(i, j);

                int[] cellType = currCell.getCellTypeImageCoords();
                int[] cellDecor = currCell.getCellDecorImageCoords();
                int[] cellActor = currCell.getCellActorImageCoords();

                ImageView imageView = ImageHandler.getTile(tileset, 1, 1);
                imageView.setFitWidth(32);
                imageView.setFitHeight(32);
                baseMap.add(imageView, i, j);

                ImageView setDecor;

                if (cellType != null) {
                    setDecor = ImageHandler.getTile(tileset, cellType[0], cellType[1]);
                    setDecor.setFitWidth(32);
                    setDecor.setFitHeight(32);

                    gridMap.add(setDecor, i, j);
                }
                if (cellDecor != null && currCell.getType() == CellType.EMPTY) {
                    setDecor = ImageHandler.getTile(tileset, cellDecor[0], cellDecor[1]);
                    setDecor.setFitWidth(32);
                    setDecor.setFitHeight(32);

                    gridMap.add(setDecor, i, j);
                }

                if (cellActor != null) {
                    ImageView setActor = ImageHandler.getTile(tileset, cellActor[0], cellActor[1]);
                    setActor.setFitWidth(32);
                    setActor.setFitHeight(32);
                    actorMap.add(setActor, i, j);
                }
            }
        }
    }

    public void printMinimap() {
        System.out.println(worldMap);
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if (canMove && isPlayerAlive) {
            switch (keyEvent.getCode()) {
                case UP -> {
                    player.move(0, -1);
                    printMap();
                }
                case DOWN -> {
                    player.move(0, 1);
                    printMap();
                }
                case LEFT -> {
                    player.move(-1, 0);
                    printMap();
                }
                case RIGHT -> {
                    player.move(1, 0);
                    printMap();
                }
                case S -> {
                    saveGame();
                }
                case E -> {
                    takeItem();
                    printMap();
                }
                case M -> {
                    ItemPickAlertScene.displayAlertBox("Mini Map", String.valueOf(worldMap), "minimap.png", 100);
                }
            }
            startFightWithEnemy();
        }
    }

    public void getEnemyMove() {
        if (canMove) {
            int[][] possibleMoves = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

            for (Actor actor : worldMap.getGameMap(worldMap.getCurrMapX(), worldMap.getCurrMapY()).getEnemyList()) {

                int[] randomCoordinates = possibleMoves[new Random().nextInt(possibleMoves.length)];
                actor.move(randomCoordinates[0], randomCoordinates[1]);
            }
        }
    }

    private void startFightWithEnemy() {
        int x = player.getCell().getX();
        int y = player.getCell().getY();
        int[][] neighbourField = {{x + 1, y}, {x - 1, y}, {x, y + 1}, {x, y - 1}};

        for (int[] i : neighbourField) {
            if (worldMap.getGameMap(worldMap.getCurrMapX(), worldMap.getCurrMapY()).getCell(i[0], i[1]).getActor() != null) {
                canMove = false;
                opponent = (Enemy) worldMap.getGameMap(worldMap.getCurrMapX(), worldMap.getCurrMapY()).getCell(i[0], i[1]).getActor();
                FightScene fightScene = new FightScene();
                fightScene.startFight();
            }
        }
    }

    private void takeItem() {
        int x = player.getCell().getX();
        int y = player.getCell().getY();
        int[][] neighbourField = {{x + 1, y}, {x - 1, y}, {x, y + 1}, {x, y - 1}};
        Cards newCard = null;

        for (int[] i : neighbourField) {
            Cell cell = worldMap.getGameMap(worldMap.getCurrMapX(), worldMap.getCurrMapY()).getCell(i[0], i[1]);
            if (Objects.equals(cell.getInteractableStatus(), true)) {
                switch (cell.getTileName()) {
                    case "key" -> player.giveKeys(1);
                    case "health" -> player.setHealth(player.getHealth() + 2);
                    case "power" -> player.setPower(player.getPower() + 2);
                    case "armor" -> player.setArmor(player.getArmor() + 2);
                    case "card" -> newCard = collectCardAndAddToDeck();
                }

                if (Objects.equals(cell.getTileName(), "card")) {
                    getCard(newCard, cell);
                } else if (Objects.equals(cell.getTileName(), "key")) {
                    getKey(cell);
                } else if (Objects.equals(cell.getTileName(), "closedDoor")) {
                    useKey(cell);
                } else if (Objects.equals(cell.getTileName(), "trapdoor")) {
                    moveToEnd();
                } else if (Objects.equals(cell.getTileName(), "dev")) {

                    if (cell.getType() == CellType.KUBA) {
                        kubaMessage();
                    }
                    if (cell.getType() == CellType.BARTEK) {
                        bartekMessage();
                    }
                    if (cell.getType() == CellType.KRZYSIEK) {
                        krzysiekMessage();
                    }

                } else {
                    getItem(cell);
                }
                loadStatistics();
            }
        }
    }

    private void kubaMessage() {
        ItemPickAlertScene.displayAlertBox("Kuba",
                "Gratulacje przybyszu! Nie sądziłem że dotrzesz do końca, a jednak \n" +
                        "udało Ci się, jestem pod wrażeniem! Droga do końca mapy była naszpikowana\n" +
                        "pułapkami, ale jak widać nie dość trudna, żeby przeszkodzić Ci w spotkaniu\n" +
                        "ze mną. Twoje imię zostanie zapamiętane na długo a bardowie będą śpiewali\n" +
                        "pieśni na Twoją cześć, jednak na razie nie dowiesz się dokąd prowadzi portal,\n" +
                        "który znajduje się za mną. Niedługo Twoja odwaga zostanie poddana\n" +
                        "kolejnej próbie, zatem musisz być czujny. Nie znasz dnia ani godziny kiedy \n" +
                        "przyjdzie czas aby znowu wyruszyć w podróż i zmierzyć się z nieprzyjaznym \n" +
                        "światem pełnym wrogów. Może wtedy poznasz tajemnice, która kryje się po \n" +
                        "drugiej stronie portalu. Zatem do zobaczenia... ", "kuba.png", 300);
    }

    private void bartekMessage() {
        ItemPickAlertScene.displayAlertBox("Bartek",
                "WOW dotarłeś!!! Uczeni wyliczyli, że jest tylko jedna szansa na milion,\n" +
                        "by zaistniało coś tak całkowicie absurdalnego. Jednak magowie obliczyli,\n" +
                        "że szanse jedna na milion sprawdzają się w dziewięciu przypadkach na dziesięć.\n" +
                        "\n" +
                        "Gratulacje!\n" +
                        "\n" +
                        "Co? Schody za mną?!\n" +
                        "Biorę je skubane na siebie!\n" +
                        "Skopię im poręcz tak bardzo, że nie będą wiedziały którędy na górę!",
                "bartek.png", 300);
    }

    private void krzysiekMessage() {
        String name = "";
        if (Objects.equals(player.getName(), "")) {
            name = "Poszukiwaczem przygód";
        } else {
            name = player.getName();
        }
        ItemPickAlertScene.displayAlertBox("Krzysiek",
                "O, moi ukochani więźniowie, muszę wymyślić wam na dziś jakieś... tortury...\n" +
                        "Oh, kim ty jesteś? " + name + "? Pierwsze słyszę, ale skoro tu dotarłeś\n" +
                        "to musisz być całkiem potężnym. Czy wiesz, że gdy zginiesz w tej krypcie to trafiasz\n" +
                        "do moich lochów? Uważaj na siebie, jeśli nie chcesz tam trafić!",
                "krzysiek.png", 300);
    }

    private void getCard(Cards newCard, Cell cell) {
        ItemPickAlertScene.displayAlertBox("Collect Item", "Great, you already collect extra card " + newCard.getName() + "!\n" +
                "Card type : " + newCard.getCardsType().name() + "\n" +
                "Card cost : " + newCard.getCardCost() + "\n" +
                "Card rarity : " + newCard.getRarity() + "\n" +
                "Description : " + newCard.getDescription() + "\n" +
                "Value : " + newCard.getValue() + "\n", newCard.getImg(), 80);
        cell.setType(CellType.EMPTY);
    }

    private void getKey(Cell cell) {
        ItemPickAlertScene.displayAlertBox("Key found!", "Hey, you've found a key! Now you can go to the final boss!", "keyBig.png", 80);
        cell.setType(CellType.EMPTY);
    }

    private void useKey(Cell cell) {
        int keyCost = 1;
        if (player.getKeyCount() >= keyCost) {
            player.useKeys(keyCost);
            cell.setType(CellType.OPEN_DOOR);
        }
    }

    private void getItem(Cell cell) {
        ItemPickAlertScene.displayAlertBox("Collect Item", "Great, you already collect extra + 2 to " +
                cell.getTileName() + "!", cell.getTileName() + ".png", 80);
        cell.setType(CellType.EMPTY);
    }

    private void moveToEnd() {
        player.moveToEnd();
    }

    public Cards collectCardAndAddToDeck() {
        CardRarity rarity = Player.drawRarity();
        CardsType cardsType = CardsType.getRandomeType();
        Cards card = new Cards(cardsType.getFile(), cardsType.getName(), null, cardsType, rarity);
        player.addCardToDeck(card);
        updateDeck();
        return card;
    }

    public void updateDeck() {
        playerCardDeck.getChildren().clear();
        for (Cards card : player.getDeck()) {
            try {
                playerCardDeck.getChildren().add(getCardPane(card));
                playerCardDeck.setAlignment(Pos.CENTER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Pane getCardPane(Cards card) throws IOException {
        Pane cardPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Card.fxml")));
        ImageView cardImage = (ImageView) cardPane.getChildren().get(0);
        ImageView cardBg = (ImageView) cardPane.getChildren().get(1);
        Label description = (Label) cardPane.getChildren().get(2);
        Label cardCost = (Label) cardPane.getChildren().get(3);

        cardImage.setImage(new Image(card.getImg()));
        description.setText(card.getDescription());
        cardCost.setText(String.valueOf(card.getCardCost()));

        return cardPane;
    }

    private void saveGame() {
        try {
            File saveFile = new File("SAVE.sav");
            FileOutputStream saveStream = new FileOutputStream(saveFile);
            ObjectOutputStream saveData = new ObjectOutputStream(saveStream);

            saveData.writeObject(worldMap);
            saveData.writeObject(player);

            saveData.close();
            saveStream.close();
            System.out.println(saveFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(": Error while saving: file not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadStatistics() {
        ObservableList<PlayerStatisticsOnMap> playerStatistics = createPlayerStatistics();
        TableView.setItems(playerStatistics);
        TableStatisticName.setCellValueFactory(cellData -> cellData.getValue().getStatisticsName());
        TableStatisticPoints.setCellValueFactory(cellData -> cellData.getValue().getStatisticsPoints().asObject());
    }

    public ObservableList<PlayerStatisticsOnMap> createPlayerStatistics() {
        ObservableList<PlayerStatisticsOnMap> playerStatistics = FXCollections.observableArrayList(
                new PlayerStatisticsOnMap("Health", player.getHealth()),
                new PlayerStatisticsOnMap("Resistance", player.getResistance()),
                new PlayerStatisticsOnMap("Armor", player.getArmor()),
                new PlayerStatisticsOnMap("Power", player.getPower()),
                new PlayerStatisticsOnMap("Exp", player.getExp()),
                new PlayerStatisticsOnMap("Keys", player.getKeyCount()));
        return playerStatistics;
    }

    @FXML
    void setFocus(MouseEvent e) {
        gridMap.requestFocus();
    }
}
