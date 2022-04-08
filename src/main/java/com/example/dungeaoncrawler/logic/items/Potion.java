package com.example.dungeaoncrawler.logic.items;

import com.example.dungeaoncrawler.logic.map.Position;

public class Potion extends Items {

    private int gainHealth;

    public Potion(String img, String name, Position position) {
        super(img, name, position);
    }

    @Override
    public void use() {

    }

}

