package com.example.springapi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.exceptions.GameAlreadyStartedException;
import com.example.exceptions.GameNotFoundException;
import com.example.service.GameService;
import com.example.springapi.model.Game;
import com.example.springapi.model.KonradStateObject;
import com.example.springapi.model.MoveObject;
import com.example.springapi.model.Game.GameState;
import com.example.springapi.model.Player.AiType;
import com.example.springapi.model.Player.PlayerColor;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    private MoveObject latesMoveObject;

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestParam(required = false) AiType aiType) {
        if (aiType == null) {
            Game newGame = gameService.createClientGame();
            return ResponseEntity.ok(newGame);
        } else {
            Game newGame = gameService.createAiGame(aiType);
            return ResponseEntity.ok(newGame);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<Game> joinGame(@RequestParam String gameId) {
        try {
            Game game = gameService.joinGame(gameId);
            return ResponseEntity.ok(game);
        } catch (GameNotFoundException | GameAlreadyStartedException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{gameId}/state")
    public ResponseEntity<Game> getGameState(@PathVariable String gameId) {
        Game game = gameService.getGameById(gameId);
        if (game != null) {
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/{gameId}/isMyTurn/{playerColor}")
    public ResponseEntity<Boolean> isMyTurn(@PathVariable String gameId, @PathVariable String playerColor) {
        Game game = gameService.getGameById(gameId);
        if (game != null) {
            if (game.getGameState() == GameState.WAITING_FOR_PLAYERS) {
                return ResponseEntity.ok(false);
            }
            return ResponseEntity.ok(gameService.isPlayerTurn(gameId, PlayerColor.valueOf(playerColor)));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{gameId}/isFinished")
    public ResponseEntity<Boolean> isGameFinished(@PathVariable String gameId) {
        Game game = gameService.getGameById(gameId);
        if (game != null) {
            return ResponseEntity.ok(game.getGameState() == GameState.FINISHED);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Game>> getGames() {
        Map<String, Game> games = gameService.getGames();
        return ResponseEntity.ok(games);
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<String> submitMove(@PathVariable String gameId, @RequestBody MoveObject move,
            @RequestParam PlayerColor playerColor) {
        Game game = gameService.getGameById(gameId);

        if (game == null) {
            return ResponseEntity.status(404).body("Game not found.");
        }

        if (!gameService.isPlayerTurn(gameId, playerColor)) {
            return ResponseEntity.status(403).body("It's not your turn.");
        }

        boolean isValidMove = gameService.processMove(game, move);
        if (!isValidMove) {
            return ResponseEntity.badRequest().body("Move not valid.");
        }

        gameService.switchTurn(game);
        this.latesMoveObject = move;

        if (game.getCurrentPlayer().isAi()) {
            System.out.println("AI move");
            gameService.playAiMove(game);
        }

        return ResponseEntity.ok("Move accepted.");
    }

    // First two cards are for Blue, second two are for Red, last one is the
    // additional card
    @PostMapping("/createGameKonrad")
    public ResponseEntity<String> createGameKonrad(@RequestParam("cards") String[] cards) {
        String gameId = gameService.createKonradGame(cards);
        return ResponseEntity.ok(gameId);

    }

    @GetMapping("/{gameId}/getLatestMove")
    public ResponseEntity<KonradStateObject> getKonradState(@PathVariable String gameId) {
        if (latesMoveObject != null) {
            KonradStateObject state = new KonradStateObject(latesMoveObject);
            return ResponseEntity.ok(state);
        }
        return ResponseEntity.badRequest().body(null);

    }

}
