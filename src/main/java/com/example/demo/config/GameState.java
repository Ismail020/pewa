package com.example.demo.config;

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
}
