package com.example.demo.models;

import com.example.demo.service.GameService;
import jakarta.persistence.Embeddable;
import java.util.List;

@Embeddable
public class GameState {
    private boolean isInProgress;
    private boolean isFinished;
    private String currentTurnHolder;
    private String gameWinner;


    public GameState() {
        this.isInProgress = false;
        this.isFinished = false;
        this.currentTurnHolder = "player1";
        this.gameWinner = null;
    }

    // Getters and setters for game state
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

    public String getGameWinner() {
        return gameWinner;
    }

    public void setGameWinner(String gameWinner) {
        this.gameWinner = gameWinner;
    }

    public void switchTurn() {
    }
}

