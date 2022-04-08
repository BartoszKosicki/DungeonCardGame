package com.example.dungeaoncrawler.logic.map;

import com.example.dungeaoncrawler.logic.actors.Actor;
import com.example.dungeaoncrawler.logic.items.Items;

import java.io.Serializable;

public class Cell implements Drawable, Serializable {

    private CellType type;
    private CellDecoration decoration;
    private Actor actor;
    private GameMap gameMap;
    private Items item;
    private int x, y;
    private char doorDirection;

    public Cell(GameMap gameMap, int x, int y, CellType type, CellDecoration decoration) {
        this.gameMap = gameMap;
        this.x = x;
        this.y = y;
        this.type = type;
        this.decoration = decoration;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public void setDecoration(CellDecoration decoration) {
        this.decoration = decoration;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }

    public Cell getNeighbor(int dx, int dy) {
        return gameMap.getCell(x + dx, y + dy);
    }

    public int[] getCellTypeImageCoords() {
        return type.getCellImageCoords();
    }

    public int[] getCellActorImageCoords() {
        if (getActor() != null) {
            return actor.getActorType().getActorImageCoords();
        }
        return null;
    }

    public int[] getCellDecorImageCoords() {
        if (decoration != null) {
            return decoration.getDecorationImageCoords();
        }
        return null;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getDoorDirection() {
        return doorDirection;
    }

    public void setDoorDirection(char direction) {
        this.doorDirection = direction;
    }

    public Items getItem() {
        return item;
    }

    public void setItem(Items item) {
        this.item = item;
    }

    @Override
    public String getTileName() {
        return this.type.getTileName();
    }

    public boolean getInteractableStatus() {
        return this.type.getInteractableStatus();
    }
}