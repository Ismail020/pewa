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

    /**
     * Notifies all clients about the current state of the queue.
     *
     * @throws JsonProcessingException if the message fails to convert to JSON
     */
    public void notifyQueueChange() throws JsonProcessingException {

        List<String> playersInQueue = getQueuePlayers(); // List of players in queue

        // Create a map to represent the structure of the message
        Map<String, Object> message = Map.of(
                "queueSize", playersInQueue.size(),
                "players", playersInQueue
        );

        // Convert the message map to JSON and send it

        messagingTemplate.convertAndSend("/topic/info", message);    }


    /**
     * Adds a player to the queue if they are not already in it, and notifies the queue change.
     * Synchronized, to ensure that only one user(so a thread) can modify/access it at the same time
     *
     * @param username the username of the player to add
     */
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

    /**
     * Removes a player from the queue and notifies the queue change.
     *
     * @param username the username of the player to remove
     */
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



    /**
     * Notifies a challenged player that they have been challenged by another player.
     *
     * @param challenger the username of the player issuing the challenge
     * @param challenged the username of the player being challenged
     */
    public void notifyChallenged(String challenger, String challenged) {
        System.out.println("yo");
        synchronized (waitingPlayers) {
            waitingPlayers.stream()
                    .filter(player -> player.equals(challenged))
                    .findFirst()
                    .ifPresent(challengedPlayer -> {

                        Map<String, String> message = new HashMap<>();
                        message.put("message", challenger);
                        messagingTemplate.convertAndSendToUser(challenged, "/queue/challenged", message);

                    });
        }
    }


    /**
     * Handles a WebSocket session disconnect by removing the user from the queue.
     *
     * @param event the session disconnect event
     */
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

    /**
     * Handles a challenge issued by one player to another. If both players are in the queue, it sends a notification.
     *
     * @param challenger the username of the player issuing the challenge
     * @param challenged the username of the player being challenged
     */
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

    /**
     * Starts a new game between two players, removes them from the queue, saves the game to the repository,
     * and notifies both players of the game's creation.
     *
     * @param player1 the username of the first player
     * @param player2 the username of the second player
     */
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
        System.out.println(savedGame.getId() + player1 + player2);
        messagingTemplate.convertAndSendToUser(player1, "/queue/pregame", Map.of("gameId", savedGame.getId(), "player1", player1, "player2", player2));
        messagingTemplate.convertAndSendToUser(player2, "/queue/pregame", Map.of("gameId", savedGame.getId(), "player1", player1, "player2", player2));
        System.out.println("queue players still remaining: " + waitingPlayers);




    }


    /**
     * Retrieves a list of all players currently in the queue.
     *
     * @return a list of usernames of players in the queue
     */
    public List<String> getQueuePlayers() {
        synchronized (waitingPlayers) {
            return new ArrayList<>(waitingPlayers);
        }
    }
}

