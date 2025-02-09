package com.example.demo.service;

import com.example.demo.models.Game;
import com.example.demo.models.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // Cache for games in memory during their lifecycle
    private final Map<Integer, Game> inMemoryGames = new HashMap<>();

    /**
     * Stores the ship locations for a specific player in the game.
     * If both players have set up their ships, it sends turn-based messages to notify them.
     *
     * @param shipLocations a list of integers representing the locations of the player's ships
     * @param playerName    the name of the player who is setting up their ships
     * @param gameId        the unique identifier of the game
     * @throws RuntimeException        if the game with the given gameId is not found
     * @throws IllegalArgumentException if the player is not part of the game
     */
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

        // 17 because that's the expected amount of filled grids per player for a battlefield layout
        if (game.getPlayer1Locations().size() == 17 && game.getPlayer2Locations().size() == 17) {
            // Generic turn messages for both players
            Map<String, Object> player1Message = Map.of(
                    "turn", true,
                    "message", String.format("It's your turn, %s! Good luck!", game.getPlayer1())
            );

            Map<String, Object> player2Message = Map.of(
                    "turn", false,
                    "message", String.format("%s will take this turn. Please wait for your turn.", game.getPlayer1())
            );

            // Send messages to both players
            sendTurnMessage("/queue/" + gameId, game.getPlayer1(), player1Message, gameId);
            sendTurnMessage("/queue/" + gameId, game.getPlayer2(), player2Message, gameId);
        }
    }

    /**
     * Sends a turn-based message to a specific player in the game.
     *
     * @param destination the destination queue or topic to which the message is sent
     * @param player      the name of the player to whom the message is sent
     * @param message     a map containing the message data to be sent
     * @param gameId      the unique identifier of the game
     */
    private void sendTurnMessage(String destination , String player, Map<String, Object> message, int gameId) {
        simpMessagingTemplate.convertAndSendToUser(player, destination , message);
        System.out.println("Message sent to " + player + ": " + message);
    }


    /**
     * Processes and validates a player's move in the game. Updates the game state based on the move,
     * sends appropriate notifications to both players, and checks if the game is over.
     *
     * @param location   the grid location the player has fired upon
     * @param playerName the name of the player making the move
     * @param gameId     the unique identifier of the game
     * @throws RuntimeException if the game with the specified gameId is not found
     */
    public void storeValidateMoves(Integer location, String playerName, int gameId  ) {

        playerName =  playerName.replace("\"", "");

        Game game = inMemoryGames.computeIfAbsent(gameId, id ->
                gameRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId))
        );


        Map<String, Object> yourTurnMessage = Map.of(
                "turn", true,
                "message", "It's your turn, Good luck!"
        );

        Map<String, Object> notYourTurnMessage = Map.of(
                "turn", false,
                "message", "Please wait for your turn."
        );


        if (playerName.equals(game.getPlayer1())) {
            game.getPlayer1ShotsFired().add(location);
            System.out.println(game.getPlayer2HitsTaken());
            System.out.println(game.getPlayer2Locations());

            boolean isHit = game.getPlayer2Locations().contains(location);
            if (isHit) {
                game.getPlayer2HitsTaken().add(location);
                game.getPlayer1HitsDealt().add(location);
                System.out.println(game.getPlayer2HitsTaken());

                //check, game over
                this.checkGameOver(game.getPlayer1(), game.getPlayer2Locations(), game.getPlayer2HitsTaken(), gameId);
            }

            boolean gameOver = game.getGameState().isFinished();


            // Send concise messages as maps
            Map<String, Object> player1Message = Map.of(
                    "result", isHit ? "hit" : "miss",
                    "location", location,
                    "message", isHit ? "You hit a target!" : "You missed!",
                    "shooter", playerName,
                    "gameOver", gameOver
            );

            Map<String, Object> player2Message = Map.of(
                    "result", isHit ? "hit" : "miss",
                    "location", location,
                    "message", isHit ? "Your ship was hit!" : "The opponent missed!",
                    "shooter", playerName,
                    "gameOver", gameOver


            );

            sendTurnMessage("/queue/game/shots", game.getPlayer1(), player1Message, gameId);
            sendTurnMessage("/queue/game/shots", game.getPlayer2(), player2Message, gameId);

            // Send messages to both players
            sendTurnMessage("/queue/" + gameId, game.getPlayer1(), notYourTurnMessage, gameId);
            sendTurnMessage("/queue/" + gameId, game.getPlayer2(), yourTurnMessage, gameId);

        }
        if (playerName.equals(game.getPlayer2())) {
            game.getPlayer2ShotsFired().add(location);

            boolean isHit = game.getPlayer1Locations().contains(location);
            if (isHit) {
                game.getPlayer1HitsTaken().add(location);
                game.getPlayer2HitsDealt().add(location);

                this.checkGameOver(game.getPlayer2(), game.getPlayer1Locations(), game.getPlayer1HitsTaken(), gameId);

            }
            boolean gameOver = game.getGameState().isFinished();


            // Send concise messages as maps
            Map<String, Object> player2Message = Map.of(
                    "result", isHit ? "hit" : "miss",
                    "location", location,
                    "message", isHit ? "You hit a target!" : "You missed!",
                    "shooter", playerName,
                    "gameOver", gameOver

            );

            Map<String, Object> player1Message = Map.of(
                    "result", isHit ? "hit" : "miss",
                    "location", location,
                    "message", isHit ? "Your ship was hit!" : "The opponent missed!",
                    "shooter", playerName,
                    "gameOver", gameOver

            );

            sendTurnMessage("/queue/game/shots", game.getPlayer1(), player1Message, gameId);
            sendTurnMessage("/queue/game/shots", game.getPlayer2(), player2Message, gameId);

            // Send messages to both players
            sendTurnMessage("/queue/" + gameId, game.getPlayer1(), yourTurnMessage, gameId);
            sendTurnMessage("/queue/" + gameId, game.getPlayer2(), notYourTurnMessage, gameId);


        }
    }

    /**
     * Checks if the game is over based on the hits taken and the remaining ship locations of a player.
     * If a player's ships have all been hit, the method determines the winner and sends a game over message.
     *
     * @param player      the player whose game state is being checked (either Player 1 or Player 2)
     * @param locations   the set of all ship locations for the player
     * @param hitsTaken   the set of locations that have been hit for the player
     * @param gameId      the unique identifier of the game
     * @throws RuntimeException if the game with the specified gameId is not found
     */
    public void checkGameOver(String player, Set<Integer> locations, Set<Integer> hitsTaken, int gameId) {
        Game game = inMemoryGames.computeIfAbsent(gameId, id ->
                gameRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId))
        );
        System.out.println("Checking if the game has ended");

        if (locations.equals(hitsTaken)) {
            game.getGameState().setFinished(true);
            game.getGameState().setGameWinner(player);

            sendTurnMessage("/queue/game/gameover", game.getPlayer1(), Map.of("winner", player), gameId);
            sendTurnMessage("/queue/game/gameover", game.getPlayer2(), Map.of("winner", player), gameId);

        }

    }

}
