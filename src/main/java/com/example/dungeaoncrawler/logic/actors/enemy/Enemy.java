package com.example.dungeaoncrawler.logic.actors.enemy;

import com.example.dungeaoncrawler.logic.map.Cell;
import com.example.dungeaoncrawler.logic.actors.Actor;
import com.example.dungeaoncrawler.logic.actors.ActorType;

import java.util.concurrent.ThreadLocalRandom;

public class Enemy extends Actor {

    private String[] attack;
    int magicMin, magicMax, physMin, physMax, poisonMin, poisonMax;


    public Enemy(int health, int resistance, int armor, int exp, int attackRound, Cell cell, String name, ActorType actorType, String[] attack,
                 int magicMin, int magicMax, int physMin, int physMax, int poisonMin, int poisonMax) {
        super(health, resistance, armor, exp, name, attackRound, actorType, cell);
        this.attack = attack;
        this.magicMin = magicMin;
        this.magicMax = magicMax;
        this.physMin = physMin;
        this.physMax = physMax;
        this.poisonMin = poisonMin;
        this.poisonMax = poisonMax;
    }

    @Override
    public String getTileName() {
        return "skeleton";
    }

    public String opponentChoseAttack() {
        return attack[ThreadLocalRandom.current().nextInt(0, attack.length)];
    }

    public int opponentAttack(String attack) {
        switch (attack) {
            case "magic" -> {
                return magicAttack();
            }
            case "damage" -> {
                return damageAttack();
            }
            case "poison" -> {
                return poisonAttack();
            }
            default -> {
                return 0;
            }
        }
    }

    private int magicAttack() {
        return ThreadLocalRandom.current().nextInt(2, 5);
    }

    private int damageAttack() {
        return ThreadLocalRandom.current().nextInt(4, 8);
    }

    private int poisonAttack() {
        return ThreadLocalRandom.current().nextInt(1, 3);
    }

}
