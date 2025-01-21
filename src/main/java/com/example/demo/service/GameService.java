package com.example.demo.service;

import com.example.demo.models.Game;
import com.example.demo.models.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

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
            System.out.println("Player 1 is now" + game.getPlayer1() + " Ships are " + game.getPlayer1Locations());
        } else if (playerName.equals(game.getPlayer2())) {
            game.addPlayer2Locations(shipLocations);
            System.out.println("Player 2 is now " + game.getPlayer2() + " Ships are " + game.getPlayer2Locations());
        } else {
            throw new IllegalArgumentException("Player not part of the game");
        }
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
