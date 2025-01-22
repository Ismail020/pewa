package com.example.demo.service;

import com.example.demo.models.Game;
import com.example.demo.models.GameRepository;
import com.example.demo.models.GameState;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.*;

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

        messagingTemplate.convertAndSend("/topic/info", message);    }


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



    public void notifyChallenged(String challenger, String challenged) {
        System.out.println("yo");
        synchronized (waitingPlayers) {
            waitingPlayers.stream()
                    .filter(player -> player.equals(challenged))
                    .findFirst()
                    .ifPresent(challengedPlayer -> {
                        // Access the attributes of the challenged player here
                        // For example:
                        Map<String, String> message = new HashMap<>();
                        message.put("message", challenger);
                        messagingTemplate.convertAndSendToUser(challenged, "/queue/challenged", message);

                    });
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
            System.out.println("no way, its being handled!");

            System.out.println(waitingPlayers);
            System.out.println(challenger);
            System.out.println(challenged);

            challenger = challenger.replace("\"", "");
            challenged = challenged.replace("\"", "");


            if (waitingPlayers.contains(challenger) && waitingPlayers.contains(challenged)) {
                System.out.println("if check works!");
                notifyChallenged(challenger , challenged);
            } else {
                messagingTemplate.convertAndSendToUser(challenger, "/queue/error", "Challenge failed. Player no longer in queue.");
            }
        }
    }

    public void startGame(String player1, String player2) {
        player2 = player2.replace("\"", "");

        // Create the game state and save it
        GameState gameState = new GameState();


        Game game = new Game(player1, player2);
        game.setGameState(gameState);

        Game savedGame = gameRepository.save(game);

        // Remove both players from the waiting list
        String finalPlayer2 = player2;
        waitingPlayers.removeIf(player -> player.equals(player1) || player.equals(finalPlayer2));

        // Notify both players with the game ID
        String gameIdMessage = "{\"gameId\": \"" + savedGame.getId() + "\"}";
        System.out.println(gameIdMessage + player1 + player2);
        messagingTemplate.convertAndSendToUser(player1, "/queue/pregame", gameIdMessage);
        messagingTemplate.convertAndSendToUser(player2, "/queue/pregame", gameIdMessage);
        System.out.println("queue players still remaining: " + waitingPlayers);




    }

    public List<String> getQueuePlayers() {
        synchronized (waitingPlayers) {
            return new ArrayList<>(waitingPlayers);
        }
    }
}

