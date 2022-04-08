package com.example.dungeaoncrawler.logic.map;

public enum RoomType {

    SPAWN("S"),
    NORMAL("■"),
    SHOP("■"),
    SPECIAL("L"),
    LAST("B"),
    FINAL("F"),
    ENDING("E");

    private final String symbol;

    RoomType (String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
