package com.example.dungeaoncrawler.logic.fight;

import com.example.dungeaoncrawler.MapController;
import com.example.dungeaoncrawler.MapScene;
import com.example.dungeaoncrawler.logic.actors.Actor;
import com.example.dungeaoncrawler.logic.actors.enemy.Enemy;
import com.example.dungeaoncrawler.logic.actors.player.Player;
import com.example.dungeaoncrawler.logic.items.cards.CardRarity;
import com.example.dungeaoncrawler.logic.items.cards.Cards;
import com.example.dungeaoncrawler.logic.items.cards.CardsCreator;
import com.example.dungeaoncrawler.logic.items.cards.CardsType;
import com.example.dungeaoncrawler.logic.actors.statusActors.CharacterAttributes;
import com.example.dungeaoncrawler.logic.actors.statusActors.LifeChanger;
import com.example.dungeaoncrawler.utils.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class FightController {

    Player player;
    Enemy opponent;
    boolean wasRolled = false;
    private int sumDiceRoll;
    private ArrayList<Cards> hand = new ArrayList<>();
    private ArrayList<Cards> winningCards;
    private boolean drawCard = false;
    private boolean playerTurn = true;
    private int handSize;
    private int handSizeModification = 0;
    private String previousMessage = "";
    private boolean isWinFight = false;

    public void initialize() {
        setPlayer();
        setOpponent();
        displayFighters();
    }

    public void displayFighters() {
        displayActorInfo(player);
        displayActorInfo(opponent);
        handSize = player.getCards();
    }

    public void setPlayer() {
        this.player = MapScene.player;
    }

    public void setOpponent() {
        this.opponent = MapController.opponent;
    }

    private void displayActorInfo(Actor actor) {
        if (actor instanceof Player) {
            displayPlayerInfo((Player) actor);
        } else {
            displayOpponentInfo(actor);
        }
    }

    private void displayPlayerInfo(Player player) {
        PlayerAttributesDisplayContainer.getItems().clear();
        ObservableList<CharacterAttributes> playerAttributes = createCharacterAttributesList(player);
        PlayerAttributesDisplayContainer.setItems(playerAttributes);
        PlayerAttributesType.setCellValueFactory(cellData -> cellData.getValue().getAttributeName());
        PlayerAttributesValue.setCellValueFactory(cellData -> cellData.getValue().getAttributeValue().asObject());
    }

    /**
     * Fill table with player attributes and its value and display it.
     *
     * @param opponent
     */
    private void displayOpponentInfo(Actor opponent) {
        OpponentAttributesDisplayContainer1.getItems().clear();
        ObservableList<CharacterAttributes> characterAttributes = createCharacterAttributesList(opponent);
        OpponentAttributesDisplayContainer1.setItems(characterAttributes);
        OpponentAttributesType1.setCellValueFactory(cellData -> cellData.getValue().getAttributeName());
        OpponentsAttributesValue1.setCellValueFactory(cellData -> cellData.getValue().getAttributeValue().asObject());
    }

    /**
     * Create List of object from characters attributes with its name and value.
     */
    private ObservableList<CharacterAttributes> createCharacterAttributesList(Actor character) {
        ObservableList<CharacterAttributes> characterAttributes = FXCollections.observableArrayList();
        characterAttributes.add(new CharacterAttributes("Health", character.getHealth()));
        characterAttributes.add(new CharacterAttributes("Armor", character.getArmor()));
        characterAttributes.add(new CharacterAttributes("Resistance", character.getResistance()));
        characterAttributes.add(new CharacterAttributes("Power", character.getPower()));
        characterAttributes.add(new CharacterAttributes("Dispel", character.getDispel()));
        return characterAttributes;
    }

    /**
     * after clicking roll dice simulate throw dice
     */
    @FXML
    void printSumDice() {
        if (!wasRolled) {
            int sumRolled = rollDice(player.getDice());
            this.sumDiceRoll = sumRolled;
            setDiceSum("You rolled " + sumRolled);
            wasRolled = true;
        }
    }

    /**
     * display message during fight - information about dealt dmg, healing etc.
     *
     * @param message text message that we want to display
     */
    public void setFightMessage(String message) {
        matchHistory.getItems().add(0, previousMessage);
        previousMessage = message;
        FightMessage.setText(message);
    }

    /**
     * game logic after picking card to play
     *
     * @param event click on card container
     */
    @FXML
    void playCard(MouseEvent event) {
        AnchorPane source = (AnchorPane) event.getSource();
        int cardIndex = Integer.parseInt(source.toString().replaceAll("[^0-9.]", ""));
        if (canPlayCard(sumDiceRoll, hand.get(cardIndex).getCardCost())) {
            String message = resolveCardEffect(hand.get(cardIndex));
            setFightMessage(message);
            source.setOpacity(0.2);
            source.setDisable(true);
            sumDiceRoll -= hand.get(cardIndex).getCardCost();
            refreshCharacterAttributes(hand.get(cardIndex));
            displayPlayerCondition();
            displayOpponentCondition();
            rollDice.setText(sumDiceRoll > 0 ? sumDiceRoll + " points remains" : "No points left.");
            checkForWin();
        } else {
            String message = "You don't have points to play this card\n";
            setFightMessage(message);
        }
    }

    @FXML
    void pickReward(MouseEvent event) {
        AnchorPane source = (AnchorPane) event.getSource();
        int cardIndex = Integer.parseInt(source.toString().replaceAll("[^0-9.]", ""));
        player.addCardToDeck(winningCards.get(cardIndex));
        winningBoard.setDisable(true);
        player.endFight();
    }

    private void displayPlayerCondition() {
        playerHealIcon.setVisible(player.isHeal());
        playerPoisonIcon.setVisible(player.isPoison());
        playerStunIcon.setVisible(player.isStuned());
        if (handSizeModification < 0) {
            extraCardIcon.setVisible(true);
        } else if (handSizeModification == 0) {
            extraCardIcon.setVisible(false);
        } else extraCardIcon.setImage(new Image("addCardDraw.gif"));
    }

    private void displayOpponentCondition() {
        opponentHealIcon.setVisible(opponent.isHeal());
        opponentPoisonIcon.setVisible(opponent.isPoison());
        opponentStunIcon.setVisible(opponent.isStuned());
    }

    private void checkForWin() {
        if (!isWinFight) {
            if (opponent.getHealth() <= 0) {
                playerIsWon();
                isWinFight = true;
            } else if (player.getHealth() <= 0) {
                opponentIsWon();
                isWinFight = true;
            }
        }
    }

    private void opponentIsWon() {
        setFightMessage(Utils.LoseFightMessage);
        endTurn.setDisable(true);
        GameBoard.setVisible(false);
        endFightButton.setVisible(true);
        player.onKill();
        player.getCell().setActor(null);
        MapController.isPlayerAlive = false;
    }

    private void playerIsWon() {
        setFightMessage(Utils.WinFightMessage);
        player.setExp(opponent.getExp());
        displayWinningScreen();
    }

    private void displayWinningScreen() {
        GameBoard.setVisible(false);
        endFightButton.setVisible(true);
        ArrayList<Cards> winningCards = generateWinningCards(3);
        ArrayList<AnchorPane> winCardContainer = createWinningCardContainer();
        displayCards(winningCards, winCardContainer);
        winningBoard.setVisible(true);
    }

    private ArrayList<AnchorPane> createWinningCardContainer() {
        ArrayList<AnchorPane> cardContainer = new ArrayList<>();
        Collections.addAll(cardContainer, winningCard0, winningCard1, winningCard2);
        return cardContainer;
    }

    private ArrayList<Cards> generateWinningCards(int cardsToGen) {
        ArrayList<Cards> listWinningCards = new ArrayList<>();
        for (int i = 0; i < cardsToGen; i++) {
            CardsType cardType = CardsType.getRandomeType();
            String imgCard = CardsCreator.imageCardCreator(cardType);
            Cards card = new Cards(imgCard, "Winning Card", null, cardType, CardRarity.genWinRandomCardRarity());
            listWinningCards.add(card);
        }
        this.winningCards = listWinningCards;
        return listWinningCards;
    }

    private void refreshCharacterAttributes(Cards card) {
        switch (card.getCardsType()) {
            case DECREASE_ARMOR, POISON, ATTACK, SPELL, STUN, DISCARD -> displayActorInfo(opponent);
            default -> displayActorInfo(player);
        }
    }

    @FXML
    void endTurn(ActionEvent event) {
        hideCardsAfterEndTurn();
        setFightMessage(Utils.endTurn);
        roundBeginning(opponent);
        opponentMove();
    }

    private void hideCardsAfterEndTurn() {
        for (int i = 0; i < cardsField.getChildren().size(); i++) {
            cardsField.getChildren().get(i).setVisible(false);
        }
    }

    private void opponentMove() {
        Thread movement = new Thread(() -> {
            if (opponent.getStun() <= 0) {
                int attackRound = opponent.getAttackRound();
                for (int i = 0; i < attackRound; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        windowBackground.setEffect(null);
                        opponentAttackPhase();
                    });
                    Platform.runLater(() -> checkForWin());

                    if (i == attackRound - 1) {
                        Platform.runLater(() -> playerNewTurnToPlay());
                    }
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                opponent.setStun(-1);
                Platform.runLater(() -> {
                    checkForWin();
                    playerNewTurnToPlay();
                });
            }
        });
        movement.start();
    }

    private void roundBeginning(Actor character) {
        checkCharacterStatus(character);
        displayActorInfo(character);
        rollDice.setText("*****  " + character.getName() + " Turn");
        checkForWin();
        displayOpponentCondition();
    }

    private void playerNewTurnToPlay() {
        if (!isWinFight) {
            roundBeginning(player);
            displayPlayerCondition();
            rollDice.setText(Utils.diceStartMessage);
            wasRolled = false;
            drawCard = false;
            endTurn.setVisible(false);
            playerTurn = false;
            windowBackground.setEffect(null);
        }
    }

    private void opponentAttackPhase() {
        if (!isWinFight) {
            String attack = opponent.opponentChoseAttack();
            int value = opponent.opponentAttack(attack);
            int healthBeforeAttack = player.getHealth();
            String message = resolveOpponentAttack(attack, value);
            displayAttackEffects(healthBeforeAttack, attack);
            displayOpponentCondition();
            displayPlayerCondition();
            setFightMessage(message);
            displayActorInfo(player);
        }
    }

    private void displayAttackEffects(int healthBeforeAttack, String message) {
        if(healthBeforeAttack > player.getHealth()){
                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setHeight(255);
                innerShadow.setWidth(255);
                innerShadow.setChoke(0.3);
                setEffectAfterAttack(message, innerShadow);
        }
    }

    private void setEffectAfterAttack(String message, InnerShadow innerShadow) {
        switch (message) {
            case "poison" -> {
                innerShadow.setColor(Color.GREENYELLOW);
                windowBackground.setEffect(innerShadow);
            }
            case "damage" -> {
                innerShadow.setColor(Color.RED);
                windowBackground.setEffect(innerShadow);
            }
            case "magic" -> {
                BoxBlur blur = new BoxBlur();
                blur.setWidth(20);
                windowBackground.setEffect(blur);
            }
        }
    }

    private String resolveOpponentAttack(String attack, int value) {
        switch (attack) {
            case "magic" -> {
                return player.takeMagicDamage(value);
            }
            case "poison" -> {
                return player.setPoison(new LifeChanger(opponent.getPower(), -value));
            }
            case "damage" -> {
                return player.takeDamage(value);
            }
        }
        return "";
    }

    private void checkCharacterStatus(Actor character) {
        int healthBeforeAttack = player.getHealth();
        character.resolveLifeChanger();
        displayAttackEffects(healthBeforeAttack, "poison");
    }

    @FXML
    void cardBringFront(MouseEvent event) {
        AnchorPane source = (AnchorPane) event.getSource();
        source.toFront();
        source.setEffect(null);
    }

    @FXML
    void setCardOpacity(MouseEvent event) {
        AnchorPane source = (AnchorPane) event.getSource();
        source.toFront();
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.8);
        source.setEffect(colorAdjust);
    }

    /**
     * After clicking draw card button player draw cards and display it on card field
     */
    @FXML
    void drawCards() {
        if (!drawCard) {
            ArrayList<Cards> newHand = drawRandomCards();
            this.hand = newHand;
            ArrayList<AnchorPane> cardContainerList = createCardContainerList();
            displayCards(newHand, cardContainerList);
            cardsField.setVisible(true);
            drawCard = true;
            endTurn.setVisible(true);
        }
    }

    /**
     * put cards property (image, cost, description etc.) into cards container.
     *
     * @param cardsToDisplay list of cards object, that player draw during draw stage
     */
    private void displayCards(ArrayList<Cards> cardsToDisplay, ArrayList<AnchorPane> gameCardsContainer) {
        for (int i = 0; i < cardsToDisplay.size(); i++) {
            AnchorPane container = gameCardsContainer.get(i);
            container.setVisible(true);
            container.setOpacity(1);
            container.setDisable(false);
            ImageView cardImage = (ImageView) container.getChildren().get(0);
            cardImage.setImage(new Image(cardsToDisplay.get(i).getImg()));
            Label cardDescription = (Label) container.getChildren().get(2);
            cardDescription.setText(cardsToDisplay.get(i).getDescription());
            Label cardCost = (Label) container.getChildren().get(3);
            cardCost.setText(String.valueOf(cardsToDisplay.get(i).getCardCost()));
        }
    }

    /**
     * @return create list card containers
     */
    private ArrayList<AnchorPane> createCardContainerList() {
        ArrayList<AnchorPane> cardContainer = new ArrayList<>();
        Collections.addAll(cardContainer, card0, card1, card2, card3, card4, card5);
        return cardContainer;
    }

    /**
     * after picking card to play
     *
     * @param card picked card
     * @return return message to display about card effect
     */
    private String resolveCardEffect(Cards card) {
        switch (card.getCardsType()) {
            case DECREASE_ARMOR -> {
                return opponent.setArmor(opponent.getArmor() - card.getValue());
            }
            case RESISTANCE -> {
                return player.setResistance(card.getValue());
            }
            case DISPEL -> {
                return player.setDispel(card.getValue());
            }
            case POISON -> {
                return opponent.setPoison(new LifeChanger(player.getPower(), -card.getValue()));
            }
            case ATTACK -> {
                return opponent.takeDamage(card.getValue());
            }
            case DISCARD -> {
                handSizeModification -= 1;
                return opponent.takeDamage(card.getValue());
            }
            case SPELL -> {
                return opponent.takeMagicDamage(card.getValue());
            }
            case ARMOR -> {
                return player.setArmor(player.getArmor() + card.getValue());
            }
            case HEAL -> {
                return player.setHeal(new LifeChanger(player.getPower(), card.getValue()));
            }
            case STUN -> {
                return opponent.setStun(player.getPower());
            }
        }
        return "";
    }

    private boolean canPlayCard(int roll, int cardCost) {
        return roll >= cardCost;
    }

    /**
     * draw card mechanism
     *
     * @return all cards that are in hand
     */
    private ArrayList<Cards> drawRandomCards() {
        Random random = new Random();
        int cardsOnHand = handSize + handSizeModification;
        cardsOnHand = getCardsOnHand(cardsOnHand);
        handSizeModification = 0;
        ArrayList<Cards> hand = new ArrayList<>();
        ArrayList<Cards> deck = player.getPlayingDeck();
        drawCardsOnHand(random, cardsOnHand, hand, deck);
        return hand;
    }

    private void drawCardsOnHand(Random random, int cardsOnHand, ArrayList<Cards> hand, ArrayList<Cards> deck) {
        for (int i = 0; i < cardsOnHand; i++) {
            if (deck.size() > cardsOnHand) {
                int index = random.nextInt(deck.size());
                hand.add(deck.get(index));
                deck.remove(index);
            } else {
                player.setPlayingDeck();
                deck = player.getPlayingDeck();
                int index = random.nextInt(deck.size());
                hand.add(deck.get(index));
                deck.remove(index);
            }
        }
    }

    private int getCardsOnHand(int cardsOnHand) {
        if (cardsOnHand >= 7) {
            cardsOnHand = 6;
        }
        if (cardsOnHand < 0) {
            cardsOnHand = 0;
        }
        return cardsOnHand;
    }

    @FXML
    void endFight() {
        player.endFight();
        Stage stage = (Stage) endFightButton.getScene().getWindow();
        opponent.getCell().setActor(null);
        opponent.onKill();
        opponent.getCell().getGameMap().removeFromEnemyList(opponent);
        opponent = null;
        MapController.canMove = true;
        MapScene.mapController.updateDeck();
        stage.close();
    }

    /**
     * simulate throwing cards
     *
     * @param dices count of dice that player has
     * @return sum of all dice rolls
     */
    private int rollDice(int dices) {
        Random random = new Random();
        int diceSum = 0;
        String message = "";
        for (int i = 0; i < dices; i++) {
            int score = random.nextInt(6) + 1;
            diceSum += score;
            message = (i + 1) + ". Dice roll = " + score + "\n";
            setFightMessage(message);
        }
        message = "You rolled " + diceSum + "\n";
        setFightMessage(message);
        return diceSum;
    }

    public void setDiceSum(String text) {
        rollDice.setText(text);
    }

    @FXML
    void showCharacterNextRoundDmg(MouseEvent event) {
        ImageView image = (ImageView) event.getSource();
        switch (image.getId()) {
            case "playerImage1" -> {
                if (opponent.getHealPts() > 0) {
                    healthInfoOpponent.setText(String.valueOf(opponent.getHealPts()));
                    healthInfoOpponent.setVisible(true);
                }
                if (opponent.getPoisonDmg() > 0) {
                    poisonInfoOpponent.setText(String.valueOf(opponent.getPoisonDmg()));
                    poisonInfoOpponent.setVisible(true);
                }
                if (opponent.getStun() > 0) {
                    stunInfoOpponent.setText(String.valueOf(opponent.getStun()));
                    stunInfoOpponent.setVisible(true);
                }
            }
            case "playerImage" -> {
                if (player.getStun() > 0) {
                    stunInfoPlayer.setText(String.valueOf(player.getStun()));
                    stunInfoPlayer.setVisible(true);
                }
                if (player.getPoisonDmg() > 0) {
                    poisonInfoPlayer.setText(String.valueOf(player.getPoisonDmg()));
                    poisonInfoPlayer.setVisible(true);
                }
                if (player.getHealPts() > 0) {
                    healthInfoPlayer.setText(String.valueOf(player.getHealPts()));
                    healthInfoPlayer.setVisible(true);
                }
                if (handSizeModification < 0) {
                    extraCardsInfoPlayer.setVisible(true);
                    extraCardsInfoPlayer.setText(String.valueOf(handSizeModification));
                }

            }
        }
    }

    @FXML
    void hideCharacterNextRoundDmg(MouseEvent event) {
        ImageView image = (ImageView) event.getSource();
        switch (image.getId()) {
            case "playerImage1" -> {
                healthInfoOpponent.setVisible(false);
                poisonInfoOpponent.setVisible(false);
                stunInfoOpponent.setVisible(false);
            }
            case "playerImage" -> {
                stunInfoPlayer.setVisible(false);
                poisonInfoPlayer.setVisible(false);
                healthInfoPlayer.setVisible(false);
                extraCardsInfoPlayer.setVisible(false);
            }
        }
    }

    @FXML
    private ImageView opponentHealIcon;

    @FXML
    private ImageView opponentPoisonIcon;

    @FXML
    private ImageView opponentStunIcon;

    @FXML
    private ImageView playerHealIcon;

    @FXML
    private ImageView playerPoisonIcon;

    @FXML
    private ImageView playerStunIcon;

    @FXML
    private ListView<String> matchHistory;

    @FXML
    private TableView<CharacterAttributes> OpponentAttributesDisplayContainer1;

    @FXML
    private TableColumn<CharacterAttributes, String> OpponentAttributesType1;

    @FXML
    private VBox OpponentStatus;

    @FXML
    private TableColumn<CharacterAttributes, Integer> OpponentsAttributesValue1;

    @FXML
    private TableView<CharacterAttributes> PlayerAttributesDisplayContainer;

    @FXML
    private TableColumn<CharacterAttributes, String> PlayerAttributesType;

    @FXML
    private TableColumn<CharacterAttributes, Integer> PlayerAttributesValue;

    @FXML
    private Label FightMessage;

    @FXML
    private AnchorPane card0;

    @FXML
    private AnchorPane card1;

    @FXML
    private ImageView cardBackground;

    @FXML
    private ImageView cardBackground2;

    @FXML
    private ImageView cardBackground3;

    @FXML
    private AnchorPane card2;

    @FXML
    private AnchorPane card3;

    @FXML
    private Label cardCost0;

    @FXML
    private Label cardCost1;

    @FXML
    private Label cardCost2;

    @FXML
    private Label cardCost3;

    @FXML
    private Label cardDescription0;

    @FXML
    private Label cardDescription1;

    @FXML
    private Label cardDescription2;

    @FXML
    private Label cardDescription3;

    @FXML
    private ImageView cardImage0;

    @FXML
    private ImageView cardImage1;

    @FXML
    private ImageView cardImage2;

    @FXML
    private ImageView cardImage3;

    @FXML
    private ImageView cardBackground1;

    @FXML
    private AnchorPane cardsField;

    @FXML
    private Button drawCards;

    @FXML
    private Button endTurn;

    @FXML
    private ImageView playerImage;

    @FXML
    private ImageView playerImage1;

    @FXML
    private Label rollDice;

    @FXML
    private ImageView windowBackground;

    @FXML
    private AnchorPane GameBoard;

    @FXML
    private AnchorPane winningBoard;

    @FXML
    private AnchorPane winningCard0;

    @FXML
    private AnchorPane winningCard1;

    @FXML
    private AnchorPane winningCard2;

    @FXML
    private Label winningCardCost0;

    @FXML
    private Label winningCardCost1;

    @FXML
    private Label winningCardCost2;

    @FXML
    private Label winningCardDescription0;

    @FXML
    private Label winningCardDescription01;

    @FXML
    private Label winningCardDescription02;

    @FXML
    private ImageView winningCardImage0;

    @FXML
    private ImageView winningCardImage1;

    @FXML
    private ImageView extraCardIcon;

    @FXML
    private ImageView winningCardImage2;

    @FXML
    private ImageView winningCardBackground0;

    @FXML
    private ImageView winningCardBackground1;

    @FXML
    private ImageView winningCardBackground2;

    @FXML
    private Button endFightButton;

    @FXML
    private ImageView cardImage4;

    @FXML
    private ImageView cardImage5;

    @FXML
    private Label cardDescription4;

    @FXML
    private Label cardDescription5;

    @FXML
    private Label cardCost4;

    @FXML
    private Label cardCost5;

    @FXML
    private AnchorPane card4;

    @FXML
    private AnchorPane card5;

    @FXML
    private ImageView cardBackground4;

    @FXML
    private ImageView cardBackground5;

    @FXML
    private Label stunInfoOpponent;

    @FXML
    private Label stunInfoPlayer;

    @FXML
    private Label poisonInfoOpponent;

    @FXML
    private Label poisonInfoPlayer;

    @FXML
    private Label healthInfoOpponent;

    @FXML
    private Label healthInfoPlayer;

    @FXML
    private Label extraCardsInfoPlayer;
}
