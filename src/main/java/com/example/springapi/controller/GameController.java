package com.example.springapi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Exceptions.GameAlreadyStartedException;
import com.example.Exceptions.GameNotFoundException;
import com.example.service.GameService;
import com.example.springapi.model.Game;
import com.example.springapi.model.Player.AiType;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

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
    public ResponseEntity<Game> getGameState(@RequestParam String gameId) {
        Game game = gameService.getGameById(gameId);
        if (game != null) {
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Game>> getGames() {
        Map<String, Game> games = gameService.getGames();
        return ResponseEntity.ok(games);
    }
}
