package com.example.demo.service;

import com.example.demo.models.Game;
import com.example.demo.models.GameRepository;
import com.example.demo.models.GameState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.Console;
import java.security.Principal;
import java.util.*;
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

    public void notifyQueueChange() throws JsonProcessingException {

        List<String> playersInQueue = getQueuePlayers(); // List of players in queue

        // Create a map to represent the structure of the message
        Map<String, Object> message = Map.of(
                "queueSize", playersInQueue.size(),
                "players", playersInQueue
        );

        // Convert the message map to JSON and send it
        String responseJson = new ObjectMapper().writeValueAsString(message);

        messagingTemplate.convertAndSend("/topic/info", responseJson);    }


    public void addPlayerToQueue(String username) {
        synchronized (waitingPlayers) {
            if (!waitingPlayers.contains(username)) {
                waitingPlayers.add(username);
                System.out.println(username + " added to queue" );

                try {
                    notifyQueueChange();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void removePlayerFromQueue(String username) {
        synchronized (waitingPlayers) {
            waitingPlayers.remove(username);
            System.out.println(username + " removed from queue" );

            try {
                notifyQueueChange();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Event listener for when a WebSocket session is disconnected
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser(); // Get the Principal object
        if (principal != null) {
            String username = principal.getName(); // Safely get the username from Principal
            System.out.println("User disconnected: " + username);
            removePlayerFromQueue(username);
        } else {
            System.out.println("User disconnected but Principal is null.");
        }
    }

    public void handleChallenge(String challenger, String challenged) {
        synchronized (waitingPlayers) {
            if (waitingPlayers.contains(challenger) && waitingPlayers.contains(challenged)) {
                waitingPlayers.remove(challenger);
                waitingPlayers.remove(challenged);
                try {
                    notifyQueueChange();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
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

