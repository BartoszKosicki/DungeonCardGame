package com.example.dungeaoncrawler.logic.items.cards;

import java.util.concurrent.ThreadLocalRandom;

public abstract class CardsCreator {

    public static int[] commonDamage = {1, 5};
    public static int[] rareDamage = {4, 8};
    public static int[] mythicDamage = {10, 25};
    public static int[] commonPoison = {1, 3};
    public static int[] rarePoison = {2, 4};
    public static int[] mythicPoison = {5, 8};
    public static String[] offensiveCardName;
    public static String[] DefensiveCardName;
    public static String[] HealCardName;
    public static String[] PoisonCardName;
    public static String[] StunCardName;

    public static int setCardValue(int[] cardsDamage) {
        return ThreadLocalRandom.current().nextInt(cardsDamage[0], cardsDamage[1]);
    }

    public static String setDescription(CardsType cardsType, int value) {
        switch (cardsType) {
            case ARMOR -> {
                return "Block next " + value + " physical damage";
            }
            case STUN -> {
                return "Stun opponent for " + value + " round";
            }
            case HEAL -> {
                return "Heal yourself at " + value + " points for turns equals your power";
            }
            case SPELL -> {
                return "Deal opponent " + value + " magical damage";
            }
            case ATTACK -> {
                return "Deal opponent " + value + " physical damage";
            }
            case POISON -> {
                return "Poison opponent at " + value + " points for turns equals your power";
            }
            case DISPEL -> {
                return "Block next " + value + " opponent spell(s)";
            }
            case RESISTANCE -> {
                return "Block next " + value + " magical damage";
            }
            case DECREASE_ARMOR -> {
                return "Decrease " + value + " opponent armor";
            }
            case DISCARD -> {
                return "Deal opponent " + value + " physical damage, pick 1 card less next ";
            }
        }
        return "do nothing";
    }

    public static String imageCardCreator(CardsType cardsType) {
        switch (cardsType) {
            case ARMOR -> {
                return "armor.gif";
            }
            case STUN -> {
                return "stun.gif";
            }
            case HEAL -> {
                return "heal.gif";
            }
            case SPELL -> {
                return "spell.gif";
            }
            case ATTACK -> {
                return "attack.gif";
            }
            case POISON -> {
                return "poisonBig.gif";
            }
            case DISPEL -> {
                return "dispel.gif";
            }
            case RESISTANCE -> {
                return "magic.gif";
            }
            case DECREASE_ARMOR -> {
                return "armorDown.gif";
            }
            default -> {
                return "swordAttack.gif";
            }
        }
    }
}
