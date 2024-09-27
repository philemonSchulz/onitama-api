package com.example.aimodels;

import java.util.LinkedList;

import com.example.springapi.controller.MoveController;
import com.example.springapi.model.Game;
import com.example.springapi.model.Move;
import com.example.springapi.model.MoveObject;
import com.example.springapi.model.Player.PlayerColor;

public class RandomAi {

    public static MoveObject getMove(Game game, PlayerColor playerColor, boolean priotizeCapturingMoves) {
        LinkedList<MoveObject> allPossibleMoves = MoveController.getAllPossibleMoves(game, playerColor);
        if (priotizeCapturingMoves) {
            return allPossibleMoves.getFirst();
        } else {
            int randomMoveIndex = (int) (Math.random() * allPossibleMoves.size());
            return allPossibleMoves.get(randomMoveIndex);
        }

    }
}
