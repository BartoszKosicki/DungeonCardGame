package com.example.dungeaoncrawler.logic.items;

import com.example.dungeaoncrawler.logic.map.Position;

import java.io.Serializable;

public abstract class Items implements Serializable {

    protected String img;
    protected String name;
    protected Position position;

    public Items(String img, String name, Position position) {
        this.img = img;
        this.name = name;
        this.position = position;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public abstract void use();
}