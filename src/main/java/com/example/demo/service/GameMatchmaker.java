package com.example.demo.service;

import com.example.demo.models.Game;
import com.example.demo.models.GameRepository;
import com.example.demo.models.GameState;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.Console;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Component
public class GameMatchmaker {
    private final Queue<String> waitingPlayers = new LinkedList<>();
    private final GameRepository gameRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GameMatchmaker(GameRepository gameRepository, SimpMessagingTemplate messagingTemplate) {
        this.gameRepository = gameRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyQueueChange() {
        List<String> playersInQueue = getQueuePlayers();
        String responseJson = String.format("{\"queueSize\":%d,\"players\":%s}", playersInQueue.size(), playersInQueue);
        messagingTemplate.convertAndSend("/topic/info", responseJson); // Broadcasting to all clients
    }


    public void addPlayerToQueue(String username) {
        synchronized (waitingPlayers) {
            if (!waitingPlayers.contains(username)) {
                waitingPlayers.add(username);
                System.out.println(username + " added to queue" );

                notifyQueueChange();
            }
        }
    }

    public void removePlayerFromQueue(String username) {
        synchronized (waitingPlayers) {
            waitingPlayers.remove(username);
            System.out.println(username + " removed from queue" );

            notifyQueueChange();
        }
    }

    public void handleChallenge(String challenger, String challenged) {
        synchronized (waitingPlayers) {
            if (waitingPlayers.contains(challenger) && waitingPlayers.contains(challenged)) {
                waitingPlayers.remove(challenger);
                waitingPlayers.remove(challenged);
                notifyQueueChange();
                startGame(challenger, challenged);
            } else {
                messagingTemplate.convertAndSendToUser(challenger, "/queue/error", "Challenge failed. Player no longer in queue.");
            }
        }
    }

    private void startGame(String player1, String player2) {
        GameState gameState = new GameState();
        gameState.setInProgress(true);

        Game game = new Game(player1, player2);
        game.setGameState(gameState);

        Game savedGame = gameRepository.save(game);

        messagingTemplate.convertAndSendToUser(player1, "/queue/game", savedGame);
        messagingTemplate.convertAndSendToUser(player2, "/queue/game", savedGame);
    }

    public List<String> getQueuePlayers() {
        synchronized (waitingPlayers) {
            return new ArrayList<>(waitingPlayers);
        }
    }
}

