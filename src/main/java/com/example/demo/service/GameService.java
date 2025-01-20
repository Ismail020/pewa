package com.example.demo.service;

import com.example.demo.models.GameState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    private final Map<String, GameState> gameStates = new HashMap<>();

    public Map<String, GameState> getGameStates() {
        return gameStates;
    }

    public Map<String, Set<Integer>> getPlayerShipLocations() {
        return playerShipLocations;
    }

    private final Map<String, Set<Integer>> playerShipLocations = new HashMap<>();

    public void storeShips(List<Integer> shipLocations, String playerName) {
        // Ensure player has a set of ship locations, if not, create a new one
        Set<Integer> currentShipLocations = playerShipLocations.computeIfAbsent(playerName, k -> new HashSet<>());

        System.out.println("To be added locations for " + playerName + ": " + shipLocations);

        // Add the new ship locations to the existing set of ship locations
        currentShipLocations.addAll(shipLocations);

        System.out.println("Current locations for " + playerName + ": " + currentShipLocations);

        // Ensure GameState is initialized for the game if not already present
        GameState gameState = gameStates.computeIfAbsent("game1", k -> new GameState());

        // Check if both players have placed their ships
            // Now, both players' ship locations are available, so we can define these variables
            Set<Integer> player1ShipLocations = playerShipLocations.get("player1");
            Set<Integer> player2ShipLocations = playerShipLocations.get("player2");

            // Ensure that player 1 and player 2 have distinct ship locations

                startGame(gameState);


    }




    private void startGame(GameState gameState) {
        if (!gameState.isInProgress()) {
            gameState.setInProgress(true);
            gameState.setCurrentTurnHolder("player1"); // Set the initial turn
            System.out.println("Game has started! Player 1's turn.");
        }
    }

    public boolean isGameStarted() {
        GameState gameState = gameStates.get("game1");
        return gameState != null && gameState.isInProgress();
    }

    public GameState getGameState() {
        return gameStates.get("game1");
    }

    public void switchTurn() {
        GameState gameState = gameStates.get("game1");
        if (gameState != null && gameState.isInProgress()) {
            gameState.switchTurn();
            System.out.println("It's now " + gameState.getCurrentTurnHolder() + "'s turn.");
        }
    }

    public void endGame(String winner) {
        GameState gameState = gameStates.get("game1");
        if (gameState != null) {
            gameState.setInProgress(false);
            gameState.setFinished(true);
            gameState.setGameWinner(winner);
            System.out.println("Game over! Winner: " + winner);
        }
    }
}

