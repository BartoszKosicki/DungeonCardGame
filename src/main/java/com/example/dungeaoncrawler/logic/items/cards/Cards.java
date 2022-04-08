package com.example.dungeaoncrawler.logic.items.cards;

import com.example.dungeaoncrawler.logic.map.Position;
import com.example.dungeaoncrawler.logic.items.Items;

public class Cards extends Items {

    private final CardsType cardsType;
    private int cardCost;
    private CardRarity rarity;
    private String description;
    private int value;

    public Cards(String img, String name, Position position, CardsType cardsType, CardRarity cardRarity) {
        super(img, name, position);
        this.cardsType = cardsType;
        this.rarity = cardRarity;
        this.cardCost = setCardCost();
        this.value = getCardValue();
        this.description = CardsCreator.setDescription(cardsType, value);
    }

    public Cards(String img, String name, Position position, CardsType cardsType, CardRarity cardRarity, int value) {
        super(img, name, position);
        this.cardsType = cardsType;
        this.rarity = cardRarity;
        this.cardCost = setCardCost();
        this.value = value;
        this.description = CardsCreator.setDescription(cardsType, value);
    }

    public void use() {

    }

    private int setCardCost() {
        switch (rarity) {
            case COMMON -> {
                return 3;
            }
            case RARE -> {
                return 6;
            }
            case MYTHIC -> {
                return 9;
            }
        }
        return 1;
    }

    private int getCardValue() {
        switch (cardsType) {
            case HEAL, POISON, DISPEL -> {
                return getTimeValue(rarity);
            }
            case ARMOR, ATTACK, DECREASE_ARMOR, SPELL, RESISTANCE -> {
                return getValue(rarity);
            }
            default -> {
                return 1;
            }
        }
    }

    private int getTimeValue(CardRarity rarity) {
        if (rarity.equals(CardRarity.COMMON)) return CardsCreator.setCardValue(CardsCreator.commonPoison);
        else if (rarity.equals(CardRarity.RARE)) return CardsCreator.setCardValue(CardsCreator.rarePoison);
        else return CardsCreator.setCardValue(CardsCreator.mythicPoison);
    }

    private int getValue(CardRarity rarity) {
        if (rarity.equals(CardRarity.COMMON)) return CardsCreator.setCardValue(CardsCreator.commonDamage);
        else if (rarity.equals(CardRarity.RARE)) return CardsCreator.setCardValue(CardsCreator.rareDamage);
        else return CardsCreator.setCardValue(CardsCreator.mythicDamage);
    }

    public CardsType getCardsType() {
        return cardsType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getCardCost() {
        return cardCost;
    }

    public void setCardCost(int cardCost) {
        this.cardCost = cardCost;
    }

    public CardRarity getRarity() {
        return rarity;
    }

    public void setRarity(CardRarity rarity) {
        this.rarity = rarity;
    }
}
