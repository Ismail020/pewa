package com.example.demo.service;

import com.example.demo.models.Game;
import com.example.demo.models.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // Cache for games in memory during their lifecycle
    private final Map<Integer, Game> inMemoryGames = new HashMap<>();

    public void storeShips(List<Integer> shipLocations, String playerName, int gameId) {
        // Retrieve the game either from cache or database
        Game game = inMemoryGames.computeIfAbsent(gameId, id ->
                gameRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId))
        );

        // Update ship locations based on the player
        if (playerName.equals(game.getPlayer1())) {
            game.addPlayer1Locations(shipLocations);
            System.out.println("Player 1 is now " + game.getPlayer1() + " Ships are " + game.getPlayer1Locations());
        } else if (playerName.equals(game.getPlayer2())) {
            game.addPlayer2Locations(shipLocations);
            System.out.println("Player 2 is now " + game.getPlayer2() + " Ships are " + game.getPlayer2Locations());
        } else {
            throw new IllegalArgumentException("Player not part of the game");
        }

        if (game.getPlayer1Locations().size() == 17 && game.getPlayer2Locations().size() == 17) {
            // Generic turn messages for both players
            String player1Message = String.format("It's your turn, %s! Good luck!", game.getPlayer1());
            String player2Message = String.format("%s will take the first turn. Please wait for your turn.", game.getPlayer1());

            // Send messages to both players
            sendTurnMessage(game.getPlayer1(), player1Message, gameId);
            sendTurnMessage(game.getPlayer2(), player2Message, gameId);
        }
    }

    private void sendTurnMessage(String player, String message, int gameId) {
        // Use SimpMessagingTemplate to send a message to the user

        simpMessagingTemplate.convertAndSendToUser(player, "/queue/" + gameId, Map.of("message", message));
        System.out.println("Message sent to " + player + ": " + message);
    }

    public Set<Integer> getPlayerShipLocations(int gameId, String playerName) {
        Game game = inMemoryGames.get(gameId);
        if (game == null) {
            throw new RuntimeException("Game not found with id: " + gameId);
        }

        if (playerName.equals(game.getPlayer1())) {
            System.out.println("Player 1 is now" + game.getPlayer1() + "Ships are " + game.getPlayer1Locations());
            return game.getPlayer1Locations();
        } else if (playerName.equals(game.getPlayer2())) {
            System.out.println("Player 2 is now" + game.getPlayer2() + "Ships are " + game.getPlayer2Locations());
            return game.getPlayer2Locations();
        } else {
            throw new IllegalArgumentException("Player not part of the game");
        }
    }
}
