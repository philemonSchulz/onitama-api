package com.example.springapi.model;

public class KonradStateObject {
    private int x;
    private int y;
    private int movementX;
    private int movementY;

    public KonradStateObject() {
    }

    public KonradStateObject(MoveObject moveObject) {
        this.x = moveObject.getPiece().getX();
        this.y = moveObject.getPiece().getY();
        this.movementX = moveObject.getMove().getX(moveObject.getPiece().getColor());
        this.movementY = moveObject.getMove().getY(moveObject.getPiece().getColor());
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
