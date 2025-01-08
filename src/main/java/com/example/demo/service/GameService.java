package com.example.demo.service;

import com.example.demo.game.GameState;
import com.example.demo.move.MoveDto;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class GameService {
    private final ConcurrentHashMap<Long, GameState> gameStates = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public GameService() {
        scheduler.scheduleAtFixedRate(this::checkTurnTimeouts, 0, 1, TimeUnit.MINUTES);
    }

    public GameState processMove(MoveDto moveDto, String playerName) {
        GameState gameState = gameStates.get(moveDto.getMatchId());

        if (!gameState.isPlayersTurn(playerName)) {
            throw new IllegalArgumentException("It's not your turn");
        }

        gameState.applyMove(moveDto, playerName);
        gameState.advanceTurn();

        return gameState;
    }

    private void checkTurnTimeouts() {
        gameStates.values().forEach(GameState::checkTurnTimeout);
    }
}
