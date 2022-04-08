package com.example.dungeaoncrawler.logic.map;

import com.example.dungeaoncrawler.logic.actors.enemy.Knight;
import com.example.dungeaoncrawler.logic.actors.enemy.Skeleton;
import com.example.dungeaoncrawler.logic.actors.enemy.SkeletonBoss;
import com.example.dungeaoncrawler.logic.actors.enemy.Wizard;
import com.example.dungeaoncrawler.logic.actors.player.Player;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.dungeaoncrawler.MapScene.player;

/**
 * A war crime
 */
public class WorldMap implements Serializable {

    private final int floor;
    private int currMapX;
    private int currMapY;
    private final int worldWidth = 11;
    private final int worldHeight = 11;
    private final GameMap[][] gameMaps = new GameMap[worldWidth][worldHeight];
    private final ArrayList<GameMap> gameMapStorage = new ArrayList<>();

    /**
     * WorldMap constructor
     *
     * @param floor Might be used in the future, numerical representation on the current floor (in case we go deeper)
     */
    public WorldMap(int floor) {
        this.floor = floor;
        placeSpawn();
        this.currMapX = gameMapStorage.get(0).getWorldPos().get(0);
        this.currMapY = gameMapStorage.get(0).getWorldPos().get(1);

        Random random = new Random();

        int normalRoomCount = random.nextInt(8, 15);
        for (int i = 0; i < normalRoomCount; i++) {
            placeMap(RoomType.NORMAL);
        }

        int specialRoomCount = random.nextInt(3, 6);
        for (int i = 0; i < specialRoomCount; i++) {
            placeMap(RoomType.SPECIAL);
        }

        placeMap(RoomType.LAST);
        placeMap(RoomType.FINAL);

        placeEnd();
    }

    /**
     * Grab GameMap from WorldMap on specified coordinates
     *
     * @param x X location of the GameMap on the WorldMap
     * @param y Y location of the GameMap on the WorldMap
     */
    public GameMap getGameMap(int x, int y) {
        return gameMaps[x][y];
    }

    public int[] getCurrentPos() {
        return new int[]{currMapX, currMapY};
    }

    /**
     * Places the spawn GameMap in the center of the WorldMap
     */
    private void placeSpawn() {

        GameMap map = MapLoader.loadMap(RoomType.SPAWN, worldWidth / 2, worldHeight / 2, this);
        map.getCell(map.getWidth() / 2, map.getHeight() / 2).setActor(player);
        player.setCell(map.getCell(map.getWidth() / 2, map.getHeight() / 2));
        gameMaps[worldWidth / 2][worldHeight / 2] = map;
        gameMapStorage.add(map);

    }

    /**
     * Places GameMap on the WorldMap
     *
     * @param roomType Used to specify what type of the room we're placing
     */
    private void placeMap(RoomType roomType) {
        ArrayList<ArrayList<ArrayList<Integer>>> possibleRoomPositions = getPossibleRoomPositions();
        ArrayList<ArrayList<Integer>> selectedCoordinates = possibleRoomPositions.get(ThreadLocalRandom.current().nextInt(0, possibleRoomPositions.size()));

        GameMap map = MapLoader.loadMap(roomType, selectedCoordinates.get(0).get(0), selectedCoordinates.get(0).get(1), this);

        CellType doorType;
        if (roomType == RoomType.LAST) {
            doorType = CellType.CLOSED_DOOR;
        } else {
            doorType = CellType.OPEN_DOOR;
        }

        if (Objects.equals(map.getWorldPos().get(0), selectedCoordinates.get(1).get(0)) && Objects.equals(map.getWorldPos().get(1) - 1, selectedCoordinates.get(1).get(1))) {
            gameMaps[selectedCoordinates.get(1).get(0)][selectedCoordinates.get(1).get(1)].addDoor('r', doorType);
            map.addDoor('l', CellType.OPEN_DOOR);
        } else if (Objects.equals(map.getWorldPos().get(0), selectedCoordinates.get(1).get(0)) && Objects.equals(map.getWorldPos().get(1) + 1, selectedCoordinates.get(1).get(1))) {
            gameMaps[selectedCoordinates.get(1).get(0)][selectedCoordinates.get(1).get(1)].addDoor('l', doorType);
            map.addDoor('r', CellType.OPEN_DOOR);
        } else if (Objects.equals(map.getWorldPos().get(0) - 1, selectedCoordinates.get(1).get(0)) && Objects.equals(map.getWorldPos().get(1), selectedCoordinates.get(1).get(1))) {
            gameMaps[selectedCoordinates.get(1).get(0)][selectedCoordinates.get(1).get(1)].addDoor('d', doorType);
            map.addDoor('u', CellType.OPEN_DOOR);
        } else {
            gameMaps[selectedCoordinates.get(1).get(0)][selectedCoordinates.get(1).get(1)].addDoor('u', doorType);
            map.addDoor('d', CellType.OPEN_DOOR);
        }

        gameMaps[map.getWorldPos().get(0)][map.getWorldPos().get(1)] = map;
        gameMapStorage.add(map);
    }

    /**
     * Gets possible room positions for the current world map.
     * It will not allow for a room to be adjacent to a room it's not connected to.
     * I sincerely hope no one ever reads this code.
     *
     * @return List of lists of pairs for room options and where they connect to
     */
    private ArrayList<ArrayList<ArrayList<Integer>>> getPossibleRoomPositions() {
        ArrayList<ArrayList<ArrayList<Integer>>> possibleRoomPositions = new ArrayList<>();
        for (GameMap gameMap : gameMapStorage) {
            if (gameMap.getRoomType() == RoomType.NORMAL || gameMap.getRoomType() == RoomType.SPAWN) {
                ArrayList<Integer> mapPos = gameMap.getWorldPos();
                for (int i = 0; i < 2; i++) {
                    for (int j = -1; j < 2; j = j + 2) {
                        int x = mapPos.get(0);
                        int y = mapPos.get(1);
                        if (gameMaps[x + j][y] == null) {
                            if (x + j >= 0 && x + j < worldWidth) {
                                if (y - 1 >= 0 && y + 1 < worldHeight && x + j - 1 >= 0 && x + j + 1 < worldWidth) {
                                    if ((gameMaps[x + j][y - 1] == null || gameMaps[x + j][y - 1] == gameMap) &&
                                            (gameMaps[x + j][y + 1] == null || gameMaps[x + j][y + 1] == gameMap) &&
                                            (gameMaps[x + j - 1][y] == null || gameMaps[x + j - 1][y] == gameMap) &&
                                            (gameMaps[x + j + 1][y] == null || gameMaps[x + j + 1][y] == gameMap)
                                    ) {
                                        ArrayList<Integer> coordsToCheck = new ArrayList<>();
                                        coordsToCheck.add(x + j);
                                        coordsToCheck.add(y);
                                        ArrayList<ArrayList<Integer>> totalCoords = new ArrayList<>();
                                        totalCoords.add(coordsToCheck);
                                        totalCoords.add(gameMap.getWorldPos());
                                        possibleRoomPositions.add(totalCoords);
                                    }
                                }
                            }
                        }
                        if (gameMaps[x][y + j] == null) {
                            if (y + j >= 0 && y + j < worldHeight) {
                                if (y + j - 1 >= 0 && y + j + 1 < worldHeight && x - 1 >= 0 && x + 1 < worldWidth) {
                                    if ((gameMaps[x - 1][y + j] == null || gameMaps[x - 1][y + j] == gameMap) &&
                                            (gameMaps[x + 1][y + j] == null || gameMaps[x + 1][y + j] == gameMap) &&
                                            (gameMaps[x][y + j - 1] == null || gameMaps[x][y + j - 1] == gameMap) &&
                                            (gameMaps[x][y + j + 1] == null || gameMaps[x][y + j + 1] == gameMap)
                                    ) {
                                        ArrayList<Integer> coordsToCheck = new ArrayList<>();
                                        coordsToCheck.add(x);
                                        coordsToCheck.add(y + j);
                                        ArrayList<ArrayList<Integer>> totalCoords = new ArrayList<>();
                                        totalCoords.add(coordsToCheck);
                                        totalCoords.add(gameMap.getWorldPos());
                                        possibleRoomPositions.add(totalCoords);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return possibleRoomPositions;
    }

    public int getCurrMapX() {
        return currMapX;
    }

    public int getCurrMapY() {
        return currMapY;
    }

    /**
     * Move to a different map in the map storage, place player on given position
     */
    public Cell mapMove(int x, int y, int pPosX, int pPosY, Player player) {
        this.currMapY += y;
        this.currMapX += x;
        Cell nextCell = getGameMap(currMapX, currMapY).getCell(pPosX, pPosY);
        nextCell.setActor(player);
        return nextCell;
    }

    public Cell moveToEnd(Player player) {
        Cell nextCell = getGameMap(0, 0).getCell(12, 10);
        nextCell.setActor(player);
        currMapX = 0;
        currMapY = 0;
        return nextCell;
    }

    /**
     * Places the true end room
     */
    private void placeEnd() {
        GameMap endMap = MapLoader.loadMap(RoomType.ENDING, 0, 0, this);
        endMap.getCell(3, 18).setType(CellType.P5);
        for (int i = 17; i >= 7; i--) {
            if ((i - 1) % 3 == 0 && i > 7) {
                endMap.getCell(3, i).setType(CellType.P3);
                endMap.getCell(2, i).setType(CellType.P2);
                endMap.getCell(1, i).setType(CellType.P1);
            } else {
                endMap.getCell(3, i).setType(CellType.P4);
            }
        }

        endMap.getCell(2, 8).setActor(new Skeleton(1, 1, 1, 1, 1, endMap.getCell(2, 8)));
        endMap.getCell(1, 11).setActor(new SkeletonBoss(1, 1, 1, 1, 1, endMap.getCell(1, 11)));
        endMap.getCell(1, 15).setActor(new Knight(1, 1, 1, 1, 1, endMap.getCell(1, 15)));
        endMap.getCell(2, 18).setActor(new Wizard(1, 1, 1, 1, 1, endMap.getCell(2, 18)));

        endMap.getEnemyList().add(endMap.getCell(2, 8).getActor());
        endMap.getEnemyList().add(endMap.getCell(1, 11).getActor());
        endMap.getEnemyList().add(endMap.getCell(1, 15).getActor());
        endMap.getEnemyList().add(endMap.getCell(2, 18).getActor());

        placeSquare(endMap, 1, 9, 1, 7);
        placeSquare(endMap, 15, 23, 1, 7);
        placeSquare(endMap, 15, 23, 10, 18);

        endMap.getCell(12, 5).setType(CellType.BARTEK);
        placeSquare(endMap, 10, 10, 1, 3);
        placeSquare(endMap, 14, 14, 1, 3);

        endMap.getCell(11, 1).setType(CellType.STAIRS1);
        endMap.getCell(12, 1).setType(CellType.STAIRS2);
        endMap.getCell(13, 1).setType(CellType.STAIRS3);
        endMap.getCell(11, 2).setType(CellType.STAIRS4);
        endMap.getCell(12, 2).setType(CellType.STAIRS5);
        endMap.getCell(13, 2).setType(CellType.STAIRS6);
        endMap.getCell(11, 3).setType(CellType.STAIRS7);
        endMap.getCell(12, 3).setType(CellType.STAIRS8);
        endMap.getCell(13, 3).setType(CellType.STAIRS9);

        placeBlockade(endMap, 10, 4);
        placeBlockade(endMap, 13, 4);

        endMap.getCell(20, 8).setType(CellType.KUBA);

        placeBlockade(endMap, 19, 9);
        placePortal(endMap, 21, 8);


        endMap.getCell(5, 12).setType(CellType.KRZYSIEK);

        gameMaps[0][0] = endMap;
        gameMapStorage.add(endMap);
    }

    private void placeSquare(GameMap map, int xS, int xE, int yS, int yE) {
        for (int x = xS; x <= xE; x++) {
            for (int y = yS; y <= yE; y++) {
                map.getCell(x, y).setType(CellType.WALL);
            }
        }
    }

    private void placeBlockade(GameMap map, int xS, int yS) {
        map.getCell(xS, yS).setType(CellType.BLOCKADE1);
        map.getCell(xS + 1, yS).setType(CellType.BLOCKADE2);
    }

    private void placePortal(GameMap map, int xS, int yS) {
        map.getCell(xS, yS).setType(CellType.PORTAL1);
        map.getCell(xS + 1, yS).setType(CellType.PORTAL2);
        map.getCell(xS, yS + 1).setType(CellType.PORTAL3);
        map.getCell(xS + 1, yS + 1).setType(CellType.PORTAL4);
    }

    /**
     * neat little toString() override :)
     */
    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder();
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                GameMap room = gameMaps[x][y];
                if (room != null) {
                    returnString.append(room.getRoomType().getSymbol()).append("  ");
                } else {
                    returnString.append("   ");
                }
            }
            returnString.append("\n");
        }
        return returnString.toString();
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }
}