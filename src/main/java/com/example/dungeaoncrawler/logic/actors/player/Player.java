package com.example.dungeaoncrawler.logic.actors.player;

import com.example.dungeaoncrawler.logic.map.Cell;
import com.example.dungeaoncrawler.logic.map.CellType;
import com.example.dungeaoncrawler.logic.actors.Actor;
import com.example.dungeaoncrawler.logic.actors.ActorType;
import com.example.dungeaoncrawler.logic.items.cards.CardRarity;
import com.example.dungeaoncrawler.logic.items.cards.Cards;
import com.example.dungeaoncrawler.logic.items.cards.CardsCreator;
import com.example.dungeaoncrawler.logic.items.cards.CardsType;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Player extends Actor {
    protected int getCards;
    protected int dice;
    protected int lvl;
    protected ArrayList<Cards> deck = new ArrayList<>();
    protected ArrayList<Cards> playingDeck;
    private int keyCount = 0;

    public Player(int health, int resistance, int armor, String idUserName, int getCards, Cell cell) {
        super(health, resistance, armor, 0, idUserName, 0, ActorType.PLAYER, cell);
        this.getCards = getCards;
        this.lvl = 1;
        dice = 3;
        setStartingDeck();
        setPlayingDeck();
    }

    public void endFight() {
        playingDeck = new ArrayList<>(deck);
        setArmor(0);
        setResistance(0);
        heal.clear();
        poison.clear();
        resetDispel();
        resetStun();
    }

    public String getTileName() {
        return "player";
    }

    public void setStartingDeck() {
        int attackCards = 4;
        int defendsCards = 4;
        int otherCards = 2;
        addOffensiveCardsToStartingDeck(attackCards);
        addDefensiveCardsToStartingDeck(defendsCards);
        addRandomCardsToStartingDeck(otherCards);
    }

    private void addOffensiveCardsToStartingDeck(int cardsNumber) {
        for (int i = 0; i < cardsNumber; i++) {
            CardRarity rarity = drawRarity();
            CardsType cardsType = CardsType.getRandomOffensive();
            String cardImg = CardsCreator.imageCardCreator(cardsType);
            deck.add(new Cards(cardImg, "attack", null, cardsType, rarity));
        }
    }

    private void addDefensiveCardsToStartingDeck(int cardsNumber) {
        for (int i = 0; i < cardsNumber; i++) {
            CardRarity rarity = drawRarity();
            CardsType cardsType = CardsType.getRandomDefence();
            String cardImg = CardsCreator.imageCardCreator(cardsType);
            deck.add(new Cards(cardImg, "attack", null, cardsType, rarity));
        }
    }

    private void addRandomCardsToStartingDeck(int cardsNumber) {
        for (int i = 0; i < cardsNumber; i++) {
            CardRarity rarity = drawRarity();
            CardsType cardsType = CardsType.getRandomeType();
            String cardImg = CardsCreator.imageCardCreator(cardsType);
            deck.add(new Cards(cardImg, "attack", null, cardsType, rarity));
        }
    }

    public static CardRarity drawRarity() {
        int faith = ThreadLocalRandom.current().nextInt(0, 11);
        if (faith <= 6) return CardRarity.COMMON;
        else if (faith <= 9) return CardRarity.RARE;
        else return CardRarity.MYTHIC;
    }

    public int getCards() {
        return getCards;
    }

    public void setGetCards(int getCards) {
        this.getCards = getCards;
    }

    public int getDice() {
        return dice;
    }

    public void setDice(int dice) {
        this.dice = dice;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int gainExp) {
        this.exp = gainExp;
    }

    public ArrayList<Cards> getDeck() {
        return deck;
    }

    public void addCardToDeck(Cards card) {
        deck.add(card);
    }

    public ArrayList<Cards> getPlayingDeck() {
        return playingDeck;
    }

    public void setPlayingDeck() {
        this.playingDeck = new ArrayList<>(deck);
    }

    public int getX() {
        return cell.getX();
    }

    public int getY() {
        return cell.getY();
    }

    public void giveKeys(int amount) {
        keyCount += amount;
    }

    public void useKeys(int cost) {
        if (keyCount >= cost) {
            keyCount -= cost;
        }
    }

    public int getKeyCount() {
        return keyCount;
    }

    public void moveToEnd() {
        Cell nextCell = cell.getGameMap().getParentMap().moveToEnd(this);
        cell.setActor(null);
        cell = nextCell;
    }

    @Override
    public void onKill() {
        this.cell.setType(CellType.GRAVE);
    }

    @Override
    public void move(int dx, int dy) {

        Cell nextCell = cell.getNeighbor(dx, dy);
        if (nextCell.getType() == CellType.EMPTY) {
            cell.setActor(null);
            nextCell.setActor(this);
            cell = nextCell;
        } else if (nextCell.getType() == CellType.OPEN_DOOR) {
            cell.setActor(null);
            switch (nextCell.getDoorDirection()) {
                case 'u' -> {
                    nextCell = cell.getGameMap().getParentMap().mapMove(-1, 0, cell.getGameMap().getWidth() / 2, 18, this);
                    cell = nextCell;
                }
                case 'd' -> {
                    nextCell = cell.getGameMap().getParentMap().mapMove(1, 0, cell.getGameMap().getWidth() / 2, 1, this);
                    cell = nextCell;
                }
                case 'l' -> {
                    nextCell = cell.getGameMap().getParentMap().mapMove(0, -1, 23, cell.getGameMap().getHeight() / 2, this);
                    cell = nextCell;
                }
                case 'r' -> {
                    nextCell = cell.getGameMap().getParentMap().mapMove(0, 1, 1, cell.getGameMap().getHeight() / 2, this);
                    cell = nextCell;
                }
            }
        }
    }
}
