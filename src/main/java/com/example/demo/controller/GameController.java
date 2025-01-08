package com.example.demo.controller;

import com.example.demo.game.GameState;
import com.example.demo.models.Ship;
import com.example.demo.move.MoveDto;
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
    private final GameService gameService;

    public GameController(GameMatchmaker matchmaker, GameService gameService) {
        this.matchmaker = matchmaker;
        this.gameService = gameService;
    }


    //@SendToUser("/queue/game") sends the response to the user-specific queue /user/{username}/queue/game
    @MessageMapping("/start")
    @SendToUser("/queue/game")
    public String startGame(Principal principal) {

        matchmaker.addPlayerToQueue(principal.getName());
        System.out.println("User added to queue: " + principal.getName());

        String responseJson = String.format("{\"message\":\"Welcome to waiting queue, %s\"}", principal.getName());


        return responseJson;
    }

    @MessageMapping("/make-move")
    @SendToUser("/queue/game")
    public String makeMove(@Payload MoveDto moveDto, Principal principal) {
        try {
            GameState gameState = gameService.processMove(moveDto, principal.getName());
            return String.format("{\"message\":\"Move accepted\", \"gameState\":%s}", gameState.toJson());
        } catch (IllegalArgumentException e) {
            return String.format("{\"error\":\"%s\"}", e.getMessage());
        }
    }

    // New method to receive the ships

//    List<Ship> ships
// New method to receive and log ships
//@MessageMapping("/ships-placed")
//@SendToUser("/queue/game")
//public String receiveShips(List<Ship> ships, Principal principal) {
//    System.out.println("Received ships from " + principal.getName());
//
//    // Log the ship information
//    for (Ship ship : ships) {
//        System.out.println("Ship Name: " + ship.getName());
//        System.out.println("Ship Size: " + ship.getSize());
//        System.out.println("Ship Locations: " + ship.getLocations());
//    }
//
//    // You can add additional logic to handle the ship placement, etc.
//    String responseJson = String.format("{\"message\":\"Ships received for %s\"}", principal.getName());
//    return responseJson;
//}



    @MessageMapping("/ships-placed")
    @SendToUser("/queue/game")
    public String receiveShips(@Payload String message, Principal principal) {
        System.out.println("Received ships from " + principal.getName());

        // Log the ship information
//        for (Ship ship : ships) {
//            System.out.println("Ship Name: " + ship.getName());
//            System.out.println("Ship Size: " + ship.getSize());
//            System.out.println("Ship Locations: " + ship.getLocations());
//        }

        // You can add additional logic to handle the ship placement, etc.
        String responseJson = String.format(message, principal.getName());
        return responseJson;
    }



}

