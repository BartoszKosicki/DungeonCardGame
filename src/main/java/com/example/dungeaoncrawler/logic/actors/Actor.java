package com.example.dungeaoncrawler.logic.actors;

import com.example.dungeaoncrawler.logic.map.Cell;
import com.example.dungeaoncrawler.logic.map.CellType;
import com.example.dungeaoncrawler.logic.map.Drawable;
import com.example.dungeaoncrawler.logic.actors.statusActors.LifeChanger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Actor implements Drawable, Serializable {
    protected Cell cell;
    protected int health = 10;
    protected ArrayList<LifeChanger> heal;
    protected ArrayList<LifeChanger> poison;
    protected int stun;
    protected int resistance;
    protected int armor;
    protected int dispel;
    protected int power;
    protected int exp;
    protected int key;
    protected String name;
    protected int attackRound;
    private final ActorType actorType;


    public Actor(int health, int resistance, int armor, int exp, String name, int attackRound, ActorType actorType, Cell cell) {
        this.health = health;
        this.resistance = resistance;
        this.armor = armor;
        this.cell = cell;
        this.heal = new ArrayList<>();
        this.poison = new ArrayList<>();
        this.stun = 0;
        this.exp = exp;
        this.attackRound = attackRound;
        this.name = name;
        this.actorType = actorType;
        power = 1;
        dispel = 0;
    }

    public ArrayList<LifeChanger> getPoison() {
        return poison;
    }

    public String setPoison(LifeChanger poison) {
        if (dispel == 0) {
            this.poison.add(poison);
            return "Player successfully poison opponent\n";
        } else {
            dispel -= 1;
            return "Opponent successfully block spell\n";
        }

    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getHealth() {
        return health;
    }

    public int getDispel() {
        return dispel;
    }

    public void resetDispel() {
        this.dispel = 0;
    }

    public ActorType getActorType() {
        return actorType;
    }

    public Cell getCell() {
        return cell;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String setHeal(LifeChanger lifeChanger) {
        this.heal.add(lifeChanger);
        return "Player successfully cast healing\n";
    }

    public int getStun() {
        return stun;
    }

    public void resetStun() {
        stun = 0;
    }

    public String setStun(int stun) {
        if (dispel <= 0) {
            this.stun += stun;
            return "Opponent is stuned\n";
        } else {
            dispel -= 1;
            return "Opponent successfully cancel player spell \n";
        }
    }

    public int getResistance() {
        return resistance;
    }

    public String setResistance(int resistance) {
        this.resistance += resistance;
        return "Player increase resistance by " + resistance + "\n";
    }

    public int getArmor() {
        return armor;
    }

    public String setArmor(int armor) {
        this.armor = armor;
        return "Armor is set on " + this.armor + " points\n";
    }

    public String takeDamage(int damage) {
        if (damage > armor) {
            int dealtDamage = damage - armor;
            health -= dealtDamage;
            armor = 0;
            return "Player deal " + dealtDamage + " damage\n";
        } else armor -= damage;
        return "Player decrease armor by " + damage + "\n";
    }


    public String takeMagicDamage(int damage) {
        if (dispel > 0) {
            dispel -= 1;
            return "Opponent block your spell\n";
        } else if (damage > resistance) {
            int dealtMagicDamage = damage - resistance;
            health -= dealtMagicDamage;
            resistance = 0;
            return "Player deal " + dealtMagicDamage + " magic damage\n";
        } else resistance -= damage;
        return "Player decrease opponent resistance by " + damage + " points\n";
    }

    public String setDispel(int dispel) {
        this.dispel += dispel;
        return "Player can now block next " + this.dispel + " spell(s)\n";
    }

    public int getExp() {
        return exp;
    }

    public void resolveLifeChanger() {
        checkPlayerStatus(poison);
        checkPlayerStatus(heal);
    }

    private void checkPlayerStatus(ArrayList<LifeChanger> list) {
        if (list.size() > 0) for (int i = 0; i < list.size(); i++) {
            LifeChanger lifeChanger = list.get(i);
            health += lifeChanger.getLifeChanger();
            if (lifeChanger.getRounds() > 0) {
                lifeChanger.setRounds(1);
            }
        }
        removeLifeChangerFromList(list);
    }

    private void removeLifeChangerFromList(List<LifeChanger> list) {
        List<LifeChanger> toRemove = list.stream()
                .filter(item -> item.getRounds() <= 0).toList();

        for (LifeChanger lifeChanger : toRemove) {
            list.remove(lifeChanger);
        }
    }

    public String getName() {
        return name;
    }

    public int getAttackRound() {
        return attackRound;
    }

    public void setCell(Cell newCell) {
        cell = newCell;
    }

    public void move(int dx, int dy) {
        Cell nextCell = cell.getNeighbor(dx, dy);
        if (nextCell.getType() == CellType.EMPTY) {
            cell.setActor(null);
            nextCell.setActor(this);
            cell = nextCell;
        }
    }

    public boolean isPoison() {
        return poison.size() > 0;
    }

    public int getPoisonDmg() {
        int sum = 0;
        if (isPoison()) {
            for (LifeChanger poisons : poison) {
                sum -= poisons.getLifeChanger();
            }
        }
        return sum;
    }

    public boolean isHeal() {
        return heal.size() > 0;
    }

    public int getHealPts() {
        int sum = 0;
        if (isHeal()) {
            for (LifeChanger heals : heal) {
                sum += heals.getLifeChanger();
            }
        }
        return sum;
    }

    public void onKill() {
        this.cell.setType(CellType.EMPTY);
    }

    public boolean isStuned() {
        return stun > 0;
    }
}

