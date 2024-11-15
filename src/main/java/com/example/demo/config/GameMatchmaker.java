package com.example.demo.config;

import com.example.demo.models.Game;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GameMatchmaker {
    private final Queue<WebSocketSession> waitingPlayers = new LinkedList<>();
    private final Map<Integer, Game> activeGames = new HashMap<>();

    public void addPlayerToQueue(WebSocketSession session) throws Exception{
        waitingPlayers.add(session);
        session.sendMessage(new TextMessage("Looking for game..."));
        if (waitingPlayers.size() >= 2) {
            WebSocketSession player1 = waitingPlayers.poll();
            WebSocketSession player2 = waitingPlayers.poll();
            startGame(player1, player2);
        }
    }

    private void startGame(WebSocketSession player1, WebSocketSession player2) {
        Game game = new Game(player1, player2);
        activeGames.put(game.getId(), game);
        // Send a message to both players to notify them the game is starting
        sendGameStartMessage(player1);
        sendGameStartMessage(player2);
    }

    private void sendGameStartMessage(WebSocketSession player) {
        try {
            player.sendMessage(new TextMessage("Game is starting!"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
