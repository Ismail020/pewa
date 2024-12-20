package com.example.demo.service;

import com.example.demo.models.Game;
import com.example.demo.models.GameRepository;
import com.example.demo.models.GameState;
import com.example.demo.models.Ship;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<Long, GameState> activeGames = new ConcurrentHashMap<>();
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void initializeGame(Game game) {
        activeGames.put(game.getId(), game.getGameState());
    }

    public boolean placeShip(Long gameId, String player, Ship ship) {
        GameState gameState = getGameState(gameId);
        return gameState.placeShip(player, ship);
    }

    public GameState getGameState(Long gameId) {
        GameState gameState = activeGames.get(gameId);
        if (gameState == null) {
            throw new IllegalArgumentException("Game not found for ID: " + gameId);
        }
        return gameState;
    }
}
