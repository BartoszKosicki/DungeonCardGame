package com.example.dungeaoncrawler.logic.actors.enemy;

import com.example.dungeaoncrawler.logic.map.Cell;
import com.example.dungeaoncrawler.logic.actors.ActorType;

public class Skeleton extends Enemy {

    public Skeleton(int health, int resistance, int armor, int exp, int attackRound, Cell cell) {
        super(health, resistance, armor, exp, attackRound, cell, "skeleton", ActorType.SKELETON, new String[]{"poison", "damage"},
                0, 0, 3, 8, 1, 3);
    }
}
