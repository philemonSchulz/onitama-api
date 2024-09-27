package com.example.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Exceptions.GameAlreadyStartedException;
import com.example.Exceptions.GameNotFoundException;
import com.example.springapi.model.Board;
import com.example.springapi.model.Game;
import com.example.springapi.model.Player;
import com.example.springapi.model.Game.GameState;
import com.example.springapi.model.Player.AiType;

@Service
public class GameService {

    private HashMap<String, Game> games;
    private int gameIndex = 0;

    public GameService() {
        games = new HashMap<>();
    }

    public Game createClientGame() {
        String gameId = "ClientGame" + gameIndex++;
        Game game = new Game();
        game.setId(gameId);
        game.setPlayerRed(new Player(Player.PlayerColor.RED));
        game.setState(GameState.WAITING_FOR_PLAYERS);
        game.setBoard(new Board());

        games.put(gameId, game);
        return game;
    }

    public Game createAiGame(AiType aiType) {
        String gameId = "AiGame" + gameIndex++;
        Game game = new Game();
        game.setId(gameId);
        game.setPlayerRed(new Player(Player.PlayerColor.RED));
        game.setPlayerBlue(new Player(Player.PlayerColor.BLUE, aiType));
        game.setBoard(new Board());
        game.setBeginTime(System.currentTimeMillis());
        game.setState(GameState.IN_PROGRESS);
        game.setCurrentPlayer(game.getBoard().getNextCard().getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                : game.getPlayerBlue());

        games.put(gameId, game);
        return game;
    }

    public Game joinGame(String gameId) throws GameNotFoundException {
        Game game = games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException("Didn't find game with id: " + gameId);
        }
        if (game.getState() != GameState.WAITING_FOR_PLAYERS) {
            throw new GameAlreadyStartedException("Game with id: " + gameId + " is not waiting for players");
        }

        game.setPlayerBlue(new Player(Player.PlayerColor.BLUE));
        game.setBeginTime(System.currentTimeMillis());
        game.setState(GameState.IN_PROGRESS);
        game.setCurrentPlayer(game.getBoard().getNextCard().getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                : game.getPlayerBlue());

        return game;
    }

    public Game getGameById(String gameId) {
        return games.get(gameId);
    }

    public HashMap<String, Game> getGames() {
        return games;
    }
}
