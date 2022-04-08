package com.example.dungeaoncrawler.logic.map;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MapLoader {

    static Random random = new Random();
    static CellType[] values = CellType.values();
    static CellDecoration[] cellDecorations = CellDecoration.values();

    public static GameMap loadMap(RoomType roomType, int worldPosX, int worldPosY, WorldMap parentMap) {
        int width = 25;
        int height = 20;
        GameMap map = new GameMap(width, height, CellType.EMPTY, roomType, worldPosX, worldPosY, parentMap);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = map.getCell(x, y);
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    cell.setType(CellType.WALL);

                }
                cell.setDecoration(randomCellDecoration(10));
            }
        }
        if (map.getRoomType() == RoomType.SPECIAL) {
            placeItems(map);
        }
        return map;
    }

    static CellType randomCellType() {
        return values[random.nextInt(values.length)];
    }

    static CellDecoration randomCellDecoration(int chance) {
        if (ThreadLocalRandom.current().nextInt(chance) == 1) {
            return cellDecorations[ThreadLocalRandom.current().nextInt(cellDecorations.length)];
        }
        return null;
    }

    public static void placeItems(GameMap map) {
        int[][] randomItemsCoordinate = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};

        for (int i = 0; i < 4; i++) {
            int[] randomPair = {ThreadLocalRandom.current().nextInt(1, 19), ThreadLocalRandom.current().nextInt(1, 19)};
            randomItemsCoordinate[i] = randomPair;
        }
        map.getCell(randomItemsCoordinate[0][0], randomItemsCoordinate[0][1]).setType(CellType.HEALTH);
        map.getCell(randomItemsCoordinate[1][0], randomItemsCoordinate[1][1]).setType(CellType.POWER);
        map.getCell(randomItemsCoordinate[2][0], randomItemsCoordinate[2][1]).setType(CellType.ARMOR);
        map.getCell(randomItemsCoordinate[3][0], randomItemsCoordinate[3][1]).setType(CellType.CARD);
    }
}
