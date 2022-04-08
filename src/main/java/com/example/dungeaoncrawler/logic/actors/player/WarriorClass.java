package com.example.dungeaoncrawler.logic.actors.player;

import com.example.dungeaoncrawler.logic.map.Cell;
import com.example.dungeaoncrawler.logic.items.cards.CardRarity;
import com.example.dungeaoncrawler.logic.items.cards.Cards;
import com.example.dungeaoncrawler.logic.items.cards.CardsCreator;
import com.example.dungeaoncrawler.logic.items.cards.CardsType;

public class WarriorClass extends Player {

    public WarriorClass(int health, int resistance, int armor, String idUserName, int getCards, Cell cell) {
        super(health, resistance, armor, idUserName, getCards, cell);
    }

    @Override
    public void setStartingDeck() {
        setSpellStartingCards(3, CardsType.ATTACK, 3);
        setSpellStartingCards(2, CardsType.ARMOR, 2);
        setSpellStartingCards(2, CardsType.DECREASE_ARMOR, 3);
        setSpellStartingCards(1, CardsType.HEAL, 2);
        setSpellStartingCards(2, CardsType.DISCARD, 5);
    }

    private void setSpellStartingCards(int cardsCount, CardsType cardsType, int value) {
        for (int i = 0; i < cardsCount; i++) {
            String cardImg = CardsCreator.imageCardCreator(cardsType);
            Cards card = new Cards(cardImg, "Spell", null, cardsType, CardRarity.COMMON, value);
            deck.add(card);
        }
    }
}
