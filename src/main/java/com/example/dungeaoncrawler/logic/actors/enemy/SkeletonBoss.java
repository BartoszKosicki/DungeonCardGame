package com.example.dungeaoncrawler.logic.actors.enemy;

import com.example.dungeaoncrawler.logic.map.Cell;
import com.example.dungeaoncrawler.logic.map.CellType;
import com.example.dungeaoncrawler.logic.actors.ActorType;

public class SkeletonBoss extends Enemy {

    public SkeletonBoss(int health, int resistance, int armor, int exp, int attackRound, Cell cell) {
        super(health, resistance, armor, exp, attackRound, cell, "skeleton boss" , ActorType.ANGRY_SKELETON, new String[]{"magic", "damage", "poison"},
                5, 11, 8, 16, 5, 9);
    }

    @Override
    public void onKill() {
        this.cell.setType(CellType.KEY);
    }
}
