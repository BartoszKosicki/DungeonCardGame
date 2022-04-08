package com.example.dungeaoncrawler.logic.actors.statusActors;

public class LifeChanger {

    private int rounds;
    private final int lifeChanger;

    public LifeChanger(int rounds, int gainLife) {
        this.rounds = rounds;
        this.lifeChanger = gainLife;
    }

    public int getRounds() {
        return rounds;
    }

    public int getLifeChanger() {
        return lifeChanger;
    }

    public void setRounds(int rounds) {
        this.rounds -= rounds;
    }
}
