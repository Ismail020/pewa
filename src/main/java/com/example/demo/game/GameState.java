package com.example.demo.game;

import com.example.demo.models.Player;
import com.example.demo.move.MoveDto;

import java.time.LocalDateTime;
import java.util.List;

public class GameState {
    private List<Player> players;
    private int currentPlayerIndex;
    private LocalDateTime lastMoveTime;
    private int[] missedTurns;

    public boolean isPlayersTurn(String playerName) {
        return players.get(currentPlayerIndex).getName().equals(playerName);
    }

    public void applyMove(MoveDto moveDto, String playerName) {
        // Apply the move logic here
    }

    public void advanceTurn() {
        lastMoveTime = LocalDateTime.now();
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void checkTurnTimeout() {
        if (lastMoveTime.plusMinutes(1).isBefore(LocalDateTime.now())) {
            missedTurns[currentPlayerIndex]++;
            if (missedTurns[currentPlayerIndex] >= 3) {
                players.remove(currentPlayerIndex);
            } else {
                advanceTurn();
            }
        }
    }

    public String toJson() {
        // Convert the game state to a JSON string
        return "{}";
    }
}
