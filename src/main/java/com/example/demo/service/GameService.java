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


    private void sendTurnMessage(String destination , String player, Map<String, Object> message, int gameId) {
        simpMessagingTemplate.convertAndSendToUser(player, destination , message);
        System.out.println("Message sent to " + player + ": " + message);
    }

//    public Set<Integer> getPlayerShipLocations(int gameId, String playerName) {
//        Game game = inMemoryGames.get(gameId);
//        if (game == null) {
//            throw new RuntimeException("Game not found with id: " + gameId);
//        }
//
//        if (playerName.equals(game.getPlayer1())) {
//            System.out.println("Player 1 is now" + game.getPlayer1() + "Ships are " + game.getPlayer1Locations());
//            return game.getPlayer1Locations();
//        } else if (playerName.equals(game.getPlayer2())) {
//            System.out.println("Player 2 is now" + game.getPlayer2() + "Ships are " + game.getPlayer2Locations());
//            return game.getPlayer2Locations();
//        } else {
//            throw new IllegalArgumentException("Player not part of the game");
//        }
//    }

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
                this.checkGameOver(game.getPlayer2(), game.getPlayer2Locations(), game.getPlayer2HitsTaken(), gameId);
            }



            // Send concise messages as maps
            Map<String, Object> player1Message = Map.of(
                    "result", isHit ? "hit" : "miss",
                    "location", location,
                    "message", isHit ? "You hit a target!" : "You missed!",
                    "player", playerName
            );

            Map<String, Object> player2Message = Map.of(
                    "result", isHit ? "hit" : "miss",
                    "location", location,
                    "message", isHit ? "Your ship was hit!" : "The opponent missed!",
                    "shooter", playerName

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
            }

            // Send concise messages as maps
            Map<String, Object> player2Message = Map.of(
                    "result", isHit ? "hit" : "miss",
                    "location", location,
                    "message", isHit ? "You hit a target!" : "You missed!",
                    "shooter", playerName
            );

            Map<String, Object> player1Message = Map.of(
                    "result", isHit ? "hit" : "miss",
                    "location", location,
                    "message", isHit ? "Your ship was hit!" : "The opponent missed!",
                    "shooter", playerName

            );

            sendTurnMessage("/queue/game/shots", game.getPlayer1(), player1Message, gameId);
            sendTurnMessage("/queue/game/shots", game.getPlayer2(), player2Message, gameId);

            // Send messages to both players
            sendTurnMessage("/queue/" + gameId, game.getPlayer1(), yourTurnMessage, gameId);
            sendTurnMessage("/queue/" + gameId, game.getPlayer2(), notYourTurnMessage, gameId);


        }
    }

    public void checkGameOver(String player, Set<Integer> locations, Set<Integer> hitsTaken, int gameId) {
        Game game = inMemoryGames.computeIfAbsent(gameId, id ->
                gameRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId))
        );

        if (game.getPlayer2().equals(player) && locations.equals(hitsTaken)){
            sendTurnMessage("/queue/" + gameId, game.getPlayer1(),  Map.of("winner", game.getPlayer1() )  , gameId);
        }
        if (Objects.equals(game.getPlayer1(), player) && locations.equals(hitsTaken)){
            sendTurnMessage("/queue/" + gameId, game.getPlayer2(),  Map.of("winner", game.getPlayer2() ) , gameId);
        }


    }

}
