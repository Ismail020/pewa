package com.example.demo.service;

import com.example.demo.models.Game;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GameMatchmaker {
    private final Queue<String> waitingPlayers = new LinkedList<>();
    private final Map<String, WebSocketSession> playerSessions = new HashMap<>();
    private final Map<Integer, Game> activeGames = new HashMap<>();
    private int gameIdCounter = 1;

    // Add a player's WebSocket session
    public void addPlayerToQueue(String username, WebSocketSession session) {
        playerSessions.put(username, session);
        waitingPlayers.add(username);

        if (waitingPlayers.size() >= 2) {
            String player1 = waitingPlayers.poll();
            String player2 = waitingPlayers.poll();
            startGame(playerSessions.get(player1), playerSessions.get(player2));
        }
    }

    // Start a game between two players
    private void startGame(WebSocketSession player1Session, WebSocketSession player2Session) {
        Game game = new Game(player1Session, player2Session);
        activeGames.put(gameIdCounter++, game);

        sendMessageToPlayer(player1Session, "Game starting with " + player2Session.getId());
        sendMessageToPlayer(player2Session, "Game starting with " + player1Session.getId());
    }


    // Send a message to a specific player
    private void sendMessageToPlayer(WebSocketSession session, String message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                System.err.println("Error sending message to  " + getPlayerName(session) + ": " + e.getMessage());
            }
        }
    }
    private String getPlayerName(WebSocketSession session) {
        return playerSessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Unknown Player");
    }
}