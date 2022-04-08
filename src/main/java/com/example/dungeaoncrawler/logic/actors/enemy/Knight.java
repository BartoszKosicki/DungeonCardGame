package com.example.dungeaoncrawler.logic.actors.enemy;

import com.example.dungeaoncrawler.logic.map.Cell;
import com.example.dungeaoncrawler.logic.actors.ActorType;

public class Knight extends Enemy {

    public Knight(int health, int resistance, int armor, int exp, int attackRound, Cell cell) {
        super(health, resistance, armor, exp, attackRound, cell, "knight", ActorType.KNIGHT, new String[]{"magic", "damage"},
                1, 6, 5, 11, 0, 0);
    }
}
