package com.example.springapi.model;

import com.example.springapi.model.Player.PlayerColor;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public class Move {
    private int x;
    private int y;

    public Move() {
    }

    @JsonCreator
    public Move(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        this.x = x;
        this.y = y;
    }

    // Explicit getters for serialization/deserialization
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Moves are mirrored for the Blue Player
    public int getX(PlayerColor playerColor) {
        if (playerColor == PlayerColor.BLUE) {
            return x * -1;
        } else {
            return x;
        }
    }

    // Moves are mirrored for the Blue Player
    public int getY(PlayerColor playerColor) {
        if (playerColor == PlayerColor.BLUE) {
            return y * -1;
        } else {
            return y;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
