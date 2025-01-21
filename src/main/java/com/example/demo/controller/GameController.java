package com.example.demo.controller;

import com.example.demo.models.Ship;
import com.example.demo.service.GameMatchmaker;
import com.example.demo.service.GameService;
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

    //private static final Pattern GAME_ID_PATTERN = Pattern.compile("^\\[([0-9]+)]$");

    private final GameMatchmaker matchmaker;
    private final GameService gameService;

    public GameController(GameMatchmaker matchmaker, GameService gameService) {
        this.matchmaker = matchmaker;
        this.gameService = gameService;
    }

    @MessageMapping("/start")
    @SendToUser("/user/queue/challenged")
    public void startGame(Principal principal, @Payload String player2) {


        matchmaker.startGame(principal.getName(), player2);
    }


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


}
