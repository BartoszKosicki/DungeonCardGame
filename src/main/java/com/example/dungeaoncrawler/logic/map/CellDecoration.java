package com.example.dungeaoncrawler.logic.map;

public enum CellDecoration {

    EMPTY(1, 1),
    FLOOR1(2, 1),
    FLOOR2(3, 1),
    FLOOR3(4, 1),
    FLOOR4(5, 1),
    GRASS1(6, 1),
    GRASS2(7, 1),
    GRASS3(8, 1),
    PLANTS1(1, 3);

    private final int x;
    private final int y;

    CellDecoration(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int[] getDecorationImageCoords() {
        return new int[]{x, y};
    }
}
