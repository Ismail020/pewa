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

        //extracting the gameId from the gameId header
        Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) headers.get("nativeHeaders");

        if (nativeHeaders != null && nativeHeaders.containsKey("gameId")) {
            // Get the gameId list (it should have only one element)
            List<String> gameIdList = nativeHeaders.get("gameId");

            // Retrieve the first element (the gameId value)
            if (!gameIdList.isEmpty()) {
                String gameIdStr = gameIdList.get(0);
                try {
                    int gameId = Integer.parseInt(gameIdStr);
                    System.out.println("Game ID: " + gameId);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid gameId format: " + gameIdStr);
                }
            } else {
                System.err.println("gameId list is empty");
            }
        } else {
            System.err.println("gameId header is missing");
        }


        String playerName = principal.getName().replace("\"", "");


//        GameService gameService = new GameService();

        // Log the ship information
        for (Ship ship : ships) {
            gameService.storeShips(ship.getLocations(), playerName);
            System.out.println(gameService.getPlayerShipLocations());

        }

    }
}
