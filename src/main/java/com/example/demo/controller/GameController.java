package com.example.demo.controller;

import com.example.demo.models.Ship;
import com.example.demo.service.GameMatchmaker;
import com.example.demo.service.GameService;
import com.example.demo.service.TimerService;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class GameController {


    private final GameMatchmaker matchmaker;
    private final TimerService timerService;
    private final GameService gameService;

    public GameController(GameMatchmaker matchmaker, TimerService timerService, GameService gameService) {
        this.matchmaker = matchmaker;
        this.timerService = timerService;
        this.gameService = gameService;
    }

    /**
     * Starts a new game between two players.
     *
     * @param principal the authenticated player initiating the game
     * @param player2   the username of the second player
     */
    @MessageMapping("/start")
    @SendToUser("/user/queue/challenged")
    public void startGame(Principal principal, @Payload String player2) {

        matchmaker.startGame(principal.getName(), player2);
    }


    /**
     * Handles the placement of ships by a player.
     *
     * @param headers   the headers containing game-related metadata, including game ID
     * @param ships     the list of ships placed by the player
     * @param principal the authenticated player who placed the ships
     */
    @MessageMapping("/ships-placed")
    @SendToUser("/queue/game")
    public void receiveShips(@Headers Map<String, Object> headers, @Payload List<Ship> ships, Principal principal) {
        Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) headers.get("nativeHeaders");
        if (nativeHeaders == null || !nativeHeaders.containsKey("gameId")) {
            throw new IllegalArgumentException("Missing gameId in headers");
        }

        String gameIdStr = nativeHeaders.get("gameId").get(0);
        int gameId;
        try {
            gameId = Integer.parseInt(gameIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid gameId format: " + gameIdStr);
        }

        String playerName = principal.getName().replace("\"", "");

        for (Ship ship : ships) {
            gameService.storeShips(ship.getLocations(), playerName, gameId);
        }
    }

    /**
     * Processes a player's shot during the game.
     *
     * @param headers   the headers containing game-related metadata, including game ID
     * @param message   the message payload containing the shot location
     * @param principal the authenticated player who took the shot
     */
    @MessageMapping("/game/shots")
    public void receiveShots(@Headers Map<String, Object> headers, @Payload Map<String, Object> message, Principal principal) {
        Integer location = (Integer) message.get("location");

        Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) headers.get("nativeHeaders");
        if (nativeHeaders == null || !nativeHeaders.containsKey("gameId")) {
            throw new IllegalArgumentException("Missing gameId in headers");
        }

        String gameIdStr = nativeHeaders.get("gameId").get(0);
        int gameId;
        try {
            gameId = Integer.parseInt(gameIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid gameId format: " + gameIdStr);
        }
        String playerName = principal.getName().replace("\"", "");

        gameService.storeValidateMoves(location, playerName, gameId );
    }


}
