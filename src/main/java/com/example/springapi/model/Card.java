package com.example.springapi.model;

import java.util.HashSet;

import com.example.springapi.model.Player.PlayerColor;

public class Card {
    private HashSet<Move> moves;
    private String name;
    private PlayerColor color;

    public Card(String name, PlayerColor color) {
        this.name = name;
        this.color = color;
        this.moves = new HashSet<>();
    }

    public Card(Card card) {
        this.name = card.name;
        this.color = card.color;
        this.moves = new HashSet<>(card.moves);
    }

    public void addMove(int x, int y) {
        moves.add(new Move(x, y));
    }

    public HashSet<Move> getMoves() {
        return moves;
    }

    public String getName() {
        return name;
    }

    public PlayerColor getColor() {
        return color;
    }

    public HashSet<Move> getAllowedMoves(int x, int y, PlayerColor playerColor) {
        HashSet<Move> allowedMoves = new HashSet<>();
        for (Move move : moves) {
            if (x + move.getX(playerColor) >= 0
                    && x + move.getX(playerColor) < Board.BOARD_SIZE
                    && y + move.getY(playerColor) >= 0
                    && y + move.getY(playerColor) < Board.BOARD_SIZE) {
                // Send back the original move, not the mirrored one
                allowedMoves.add(new Move(move.getX(PlayerColor.RED), move.getY(PlayerColor.RED)));
            }
        }
        return allowedMoves;
    }
}
