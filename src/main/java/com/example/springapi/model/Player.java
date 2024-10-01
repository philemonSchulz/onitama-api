package com.example.springapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {
    public enum PlayerColor {
        RED, BLUE
    }

    public enum AiType {
        RANDOM, RANDOM_PRIOTIZING, MCTS
    }

    private PlayerColor color;
    private AiType aiType;

    public Player() {
    }

    @JsonCreator
    public Player(@JsonProperty("color") PlayerColor color) {
        this.color = color;
    }

    public Player(@JsonProperty("color") PlayerColor color, @JsonProperty("aiType") AiType aiType) {
        this.color = color;
        this.aiType = aiType;
    }

    public PlayerColor getColor() {
        return color;
    }

    public AiType getAiType() {
        return aiType;
    }

    public boolean isPlayerRed() {
        return color == PlayerColor.RED;
    }

    public boolean isPlayerBlue() {
        return color == PlayerColor.BLUE;
    }

    public boolean isAi() {
        return aiType != null;
    }
}
