package com.example.demo.controller;

import com.example.demo.models.GameRepository;
import com.example.demo.models.Ship;
import com.example.demo.service.GameMatchmaker;
import com.example.demo.service.GameService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class GameController {

    private final GameMatchmaker matchmaker;

    public GameController(GameMatchmaker matchmaker) {
        this.matchmaker = matchmaker;
    }

    @MessageMapping("/start")
    @SendToUser("/user/queue/challenged")
    public void startGame(Principal principal, @Payload String player2) {


        matchmaker.startGame(principal.getName(), player2);
    }


    @MessageMapping("/ships-placed")
    @SendToUser("/queue/game")
    public void receiveShips(List<Ship> ships, Principal principal) {

        System.out.println("Received ships from " + principal.getName());

        String playerName = principal.getName().replace("\"", "");


        GameService gameService = new GameService();

        // Log the ship information
        for (Ship ship : ships) {
            gameService.storeShips(ship.getLocations(), playerName);
            System.out.println(gameService.getPlayerShipLocations());

        }

    }
}
