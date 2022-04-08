package com.example.dungeaoncrawler.logic.map;

import com.example.dungeaoncrawler.logic.actors.Actor;
import com.example.dungeaoncrawler.logic.actors.player.Player;
import com.example.dungeaoncrawler.logic.actors.enemy.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameMap implements Serializable {

    private final int width;
    private final int height;
    private Cell[][] cells;
    private final RoomType roomType;
    private Cell[][] actorCells;
    private final int worldPosX;
    private final int worldPosY;
    private final int potentialSpawns = ThreadLocalRandom.current().nextInt(3);
    private final int potentialDecorations = ThreadLocalRandom.current().nextInt(20);
    private final WorldMap parentMap;
    private final List<Actor> enemyList = new ArrayList<>();

    private Player player;
    private Actor enemy;

    public GameMap(int width, int height, CellType defaultCellType, RoomType roomType, int worldPosX, int worldPosY, WorldMap parentMap) {
        this.parentMap = parentMap;
        this.width = width;
        this.height = height;
        this.roomType = roomType;
        this.worldPosX = worldPosX;
        this.worldPosY = worldPosY;
        cells = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(this, x, y, defaultCellType, CellDecoration.EMPTY);
            }
        }
        if (roomType == RoomType.NORMAL) {
            placeEnemy();
        }

        if (roomType == RoomType.FINAL) {
            placeBoss();
        }

        if (roomType == RoomType.LAST) {
            placeExit();
        }
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void placeEnemy() {
        int[] randomPlace = {0, 0};
        int randomCase = ThreadLocalRandom.current().nextInt(1, 4);

        randomPlace[0] = ThreadLocalRandom.current().nextInt(1, 24);
        randomPlace[1] = ThreadLocalRandom.current().nextInt(1, 19);

        switch (randomCase) {
            case 1 -> {
                Actor skeleton = new Skeleton(10, 0, 0, 10, 2, cells[randomPlace[0]][randomPlace[1]]);
                cells[randomPlace[0]][randomPlace[1]].setActor(skeleton);
                enemyList.add(skeleton);
            }
            case 2 -> {
                Actor wizard = new Wizard(10, 15, 5, 10, 2, cells[randomPlace[0]][randomPlace[1]]);
                cells[randomPlace[0]][randomPlace[1]].setActor(wizard);
                enemyList.add(wizard);
            }
            case 3 -> {
                Actor knight = new Knight(10, 5, 15, 10, 2, cells[randomPlace[0]][randomPlace[1]]);
                cells[randomPlace[0]][randomPlace[1]].setActor(knight);
                enemyList.add(knight);
            }
        }
    }

    private void placeBoss() {
        Actor skeletonBoss = new SkeletonBoss(250, 50, 50, 250, 2, cells[width / 2][height / 2]);
        cells[width / 2][height / 2].setActor(skeletonBoss);
        enemyList.add(skeletonBoss);
    }

    private void placeExit() {
        cells[width / 2][height / 2].setType(CellType.TRAPDOOR);
    }

    public ArrayList<Integer> getWorldPos() {
        ArrayList<Integer> worldPos = new ArrayList<>();
        worldPos.add(worldPosX);
        worldPos.add(worldPosY);
        return worldPos;
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addDoor(char direction, CellType doorType) {
        switch (direction) {
            case 'u': {
                cells[width / 2][0].setType(doorType);
                cells[width / 2][0].setDoorDirection('u');
                break;
            }
            case 'd': {
                cells[width / 2][height - 1].setType(doorType);
                cells[width / 2][height - 1].setDoorDirection('d');
                break;
            }
            case 'l': {
                cells[0][height / 2].setType(doorType);
                cells[0][height / 2].setDoorDirection('l');
                break;
            }
            case 'r': {
                cells[width - 1][height / 2].setType(doorType);
                cells[width - 1][height / 2].setDoorDirection('r');
                break;
            }
        }
    }

    public WorldMap getParentMap() {
        return parentMap;
    }

    public Player getPlayer() {
        return player;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Actor getEnemy() {
        return enemy;
    }

    public List<Actor> getEnemyList() {
        return enemyList;
    }

    public void removeFromEnemyList(Enemy enemy) {
        enemyList.remove(enemy);
    }
}