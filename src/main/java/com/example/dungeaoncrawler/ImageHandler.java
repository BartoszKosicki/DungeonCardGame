package com.example.dungeaoncrawler;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class ImageHandler {
    public ImageView getTile (Image tileset, int x, int y) {
        int tileWidth = 32;
        int tileSpacing = 2;
        Rectangle2D croppedArea = new Rectangle2D(x*(tileWidth + tileSpacing), y*(tileWidth + tileSpacing), tileWidth, tileWidth);
        ImageView tile = new ImageView(tileset);
        tile.setViewport(croppedArea);
        return tile;
    }
}
