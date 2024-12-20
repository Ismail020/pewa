package com.example.demo.models;

import com.example.demo.service.GameService;
import jakarta.persistence.Embeddable;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class GameState {

    public String gameId;
    private boolean isInProgress;
    private boolean isFinished;

    private String currentTurnHolder;
    private String gameWinner;

    private String[][] player1Board;
    private String[][] player2Board;

    private List<String> player1moveHistory;
    private List<String> player2moveHistory;

    private GameService gameService;
    private boolean player1Ready;
    private boolean player2Ready;

    public GameState(){

    }

    public GameState(String player1, String player2) {
        this.gameId = null;
        this.isInProgress = false;
        this.isFinished = false;
        this.currentTurnHolder = player1;
        this.gameWinner = null;
        this.player1Ready = false;
        this.player2Ready = false;
        this.player1moveHistory = new ArrayList<>();
        this.player2moveHistory = new ArrayList<>();
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setInProgress(boolean inProgress) {
        isInProgress = inProgress;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getCurrentTurnHolder() {
        return currentTurnHolder;
    }

    public void setCurrentTurnHolder(String currentTurnHolder) {
        this.currentTurnHolder = currentTurnHolder;
    }

    public void switchTurn() {
        this.currentTurnHolder = this.currentTurnHolder.equals("player1") ? "player2" : "player1";
    }

    public String getGameWinner() {
        return gameWinner;
    }

    public void setGameWinner(String gameWinner) {
        this.gameWinner = gameWinner;
    }

    public boolean isPlayer1Ready() {
        return player1Ready;
    }

    public void setPlayer1Ready(boolean player1Ready) {
        this.player1Ready = player1Ready;
    }

    public boolean isPlayer2Ready() {
        return player2Ready;
    }

    public void setPlayer2Ready(boolean player2Ready) {
        this.player2Ready = player2Ready;
    }


    public boolean placeShip(String player, Ship ship) {
        // Implement ship placement logic here
        // Example: Check boundaries, collisions, etc.
        return true; // Return true if successfully placed, false otherwise
    }
}
