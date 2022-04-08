package com.example.dungeaoncrawler.logic.items.cards;

import java.util.concurrent.ThreadLocalRandom;

public enum CardRarity {

    COMMON,
    RARE,
    MYTHIC;

    public static CardRarity genWinRandomCardRarity() {
        return values()[ThreadLocalRandom.current().nextInt(1, 3)];
    }
}
