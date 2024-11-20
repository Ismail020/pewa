package com.example.demo.service;

import com.example.demo.models.Game;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Component
public class GameMatchmaker {
    private final Queue<String> waitingPlayers = new LinkedList<>();
    private final Map<Integer, Game> activeGames = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public GameMatchmaker(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void addPlayerToQueue(String username) {
        waitingPlayers.add(username);
        if (waitingPlayers.size() >= 2) {
            String player1 = waitingPlayers.poll();
            String player2 = waitingPlayers.poll();
            startGame(player1, player2);
        }
    }

    private void startGame(String player1, String player2) {
        messagingTemplate.convertAndSendToUser(player1, "/queue/game", "Game starting with " + player2);
        messagingTemplate.convertAndSendToUser(player2, "/queue/game", "Game starting with " + player1);
    }
}

