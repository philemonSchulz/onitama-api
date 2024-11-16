package com.example.springapi.model;

public class KonradStateObject {
    private int x;
    private int y;
    private int movementX;
    private int movementY;

    public KonradStateObject() {
    }

    public KonradStateObject(int x, int y, int movementX, int movementY) {
        this.x = x;
        this.y = y;
        this.movementX = movementX;
        this.movementY = movementY;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
