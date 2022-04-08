package com.example.dungeaoncrawler.logic.actors.enemy;

import com.example.dungeaoncrawler.logic.map.Cell;
import com.example.dungeaoncrawler.logic.actors.ActorType;

public class Wizard extends Enemy {

    public Wizard(int health, int resistance, int armor, int exp, int attackRound, Cell cell) {
        super(health, resistance, armor, exp, attackRound, cell, "wizard", ActorType.WIZARD, new String[]{"magic", "damage"},
                1, 10, 1, 3, 0, 0);
    }
}
