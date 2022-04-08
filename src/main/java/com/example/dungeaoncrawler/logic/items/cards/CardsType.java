package com.example.dungeaoncrawler.logic.items.cards;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public enum CardsType {

    SPELL("spell", "spell.gif"),
    ATTACK("attack", "attack.gif"),
    POISON("poison", "poisonBig.gif"),
    ARMOR("armor", "armor.gif"),
    RESISTANCE("resistance", "resistance.gif"),
    DISPEL("dispel", "dispel.gif"),
    DECREASE_ARMOR("armorDown", "armorDown.gif"),
    HEAL("heal", "heal.gif"),
    STUN("stun", "stun.gif"),
    DISCARD("discard", "lessCard.gif");

    private final String name;
    private final String file;

    CardsType(String name, String file) {
        this.name = name;
        this.file = file;
    }

    public static CardsType getRandomOffensive() {
        Random random = new Random();
        return values()[random.nextInt(3)];
    }

    public static CardsType getRandomDefence() {
        Random random = new Random();
        return values()[ThreadLocalRandom.current().nextInt(3, 6)];
    }

    public static CardsType getRandomeType() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }
}
