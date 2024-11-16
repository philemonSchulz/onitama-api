package com.example.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.aimodels.RandomAi;
import com.example.exceptions.GameAlreadyStartedException;
import com.example.exceptions.GameNotFoundException;
import com.example.springapi.model.Board;
import com.example.springapi.model.Card;
import com.example.springapi.model.Game;
import com.example.springapi.model.MoveObject;
import com.example.springapi.model.Piece;
import com.example.springapi.model.Piece.PieceType;
import com.example.springapi.model.Player;
import com.example.springapi.model.Game.GameState;
import com.example.springapi.model.Player.AiType;
import com.example.springapi.model.Player.PlayerColor;
import com.example.springapi.model.Tile;

@Service
public class GameService {

    private HashMap<String, Game> games;
    private int gameIndex = 0;

    public GameService() {
        games = new HashMap<>();
    }

    /*
     * Methods for Rest API from here:
     */

    public Game createClientGame() {
        String gameId = "ClientGame" + gameIndex++;
        Game game = new Game(gameId);
        game.setPlayerRed(new Player(Player.PlayerColor.RED));
        game.setGameState(GameState.WAITING_FOR_PLAYERS);

        games.put(gameId, game);
        return game;
    }

    public Game createAiGame(AiType aiType) {
        String gameId = "AiGame" + gameIndex++;
        Game game = new Game(gameId);
        game.setPlayerRed(new Player(Player.PlayerColor.RED));
        game.setPlayerBlue(new Player(Player.PlayerColor.BLUE, aiType));
        game.setBeginTime(System.currentTimeMillis());
        game.setGameState(GameState.IN_PROGRESS);
        game.setCurrentPlayer(game.getNextCard().getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                : game.getPlayerBlue());

        games.put(gameId, game);

        if (game.getCurrentPlayer().getColor() == PlayerColor.BLUE) {
            playAiMove(game);
        }

        return game;
    }

    public String createKonradGame(String[] cards) {
        String gameId = "KonradGame" + gameIndex++;
        Game game = new Game(gameId);
        game.setPlayerRed(new Player(Player.PlayerColor.RED));
        game.setPlayerBlue(new Player(Player.PlayerColor.BLUE));
        game.setBeginTime(System.currentTimeMillis());
        game.setGameState(GameState.WAITING_FOR_PLAYERS);

        Card[] cardObjects = CardCreator.createCardsBasedOnNames(cards);
        ArrayList<Card> playerRedCards = new ArrayList<>();
        ArrayList<Card> playerBlueCards = new ArrayList<>();
        playerBlueCards.add(cardObjects[0]);
        playerBlueCards.add(cardObjects[1]);
        playerRedCards.add(cardObjects[2]);
        playerRedCards.add(cardObjects[3]);
        game.setPlayerRedCards(playerRedCards);
        game.setPlayerBlueCards(playerBlueCards);
        game.setNextCard(cardObjects[4]);

        game.setCurrentPlayer(cardObjects[4].getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                : game.getPlayerBlue());

        games.put(gameId, game);

        return gameId;
    }

    public Game joinGame(String gameId) throws GameNotFoundException {
        Game game = games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException("Didn't find game with id: " + gameId);
        }
        if (game.getGameState() != GameState.WAITING_FOR_PLAYERS) {
            throw new GameAlreadyStartedException("Game with id: " + gameId + " is not waiting for players");
        }

        game.setPlayerBlue(new Player(Player.PlayerColor.BLUE));
        game.setBeginTime(System.currentTimeMillis());
        game.setGameState(GameState.IN_PROGRESS);
        game.setCurrentPlayer(game.getNextCard().getColor() == Player.PlayerColor.RED ? game.getPlayerRed()
                : game.getPlayerBlue());

        return game;
    }

    /*
     * Methods for Game Logic from here:
     */

    public boolean processMove(Game game, MoveObject move) {
        PlayerColor playerColor = game.getCurrentPlayer().getColor();
        Tile tile = game.getBoard().getTile(move.getPiece().getX(), move.getPiece().getY());
        Tile targetTile = game.getBoard().getTile(move.getPiece().getX() + move.getMove().getX(playerColor),
                move.getPiece().getY() + move.getMove().getY(playerColor));

        boolean removedPieceWasMaster = false;

        if (targetTile.getPiece() != null && targetTile.getPiece().getColor() != playerColor) {
            if (playerColor == PlayerColor.RED) {
                game.getPlayerBluePieces().remove(targetTile.getPiece());
            } else {
                game.getPlayerRedPieces().remove(targetTile.getPiece());
            }
            if (targetTile.getPiece().getType() == PieceType.MASTER) {
                removedPieceWasMaster = true;
            }
            targetTile.removePiece();
        } else if (targetTile.getPiece() != null && targetTile.getPiece().getColor() == playerColor) {
            return false;
        }

        Piece piece = tile.removePiece();
        targetTile.setPiece(piece);

        if (targetTile.isTempleReached() || removedPieceWasMaster) {
            game.setGameState(playerColor == PlayerColor.RED ? GameState.FINISHED : GameState.FINISHED);
        }

        return true;
    }

    public void switchTurn(Game game) {
        game.setCurrentPlayer(game.getCurrentPlayer().getColor() == Player.PlayerColor.RED ? game.getPlayerBlue()
                : game.getPlayerRed());
        game.setTurnStartTime(System.currentTimeMillis());
    }

    public void playAiMove(Game game) {
        if (game.getGameState() == GameState.IN_PROGRESS && game.getPlayerBlue().isAi()) {
            MoveObject move = generateAiMove(game);
            processMove(game, move);
            switchTurn(game);
        }
    }

    private MoveObject generateAiMove(Game game) {
        switch (game.getPlayerBlue().getAiType()) {
            case RANDOM:
                return RandomAi.getMove(game, Player.PlayerColor.BLUE, false);
            case RANDOM_PRIOTIZING:
                return RandomAi.getMove(game, Player.PlayerColor.BLUE, true);
            case MCTS:
            default:
                return RandomAi.getMove(game, Player.PlayerColor.BLUE, false);
        }
    }

    public boolean isPlayerTurn(String gameId, PlayerColor playerColor) {
        Game game = games.get(gameId);
        return game.getCurrentPlayer().getColor() == playerColor;
    }

    /*
     * Setter and Getter from here:
     */

    public Game getGameById(String gameId) {
        return games.get(gameId);
    }

    public HashMap<String, Game> getGames() {
        return games;
    }
}
