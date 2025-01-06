package com.example.demo.controller;

import com.example.demo.models.Ship;
import com.example.demo.service.GameMatchmaker;
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


    //@SendToUser("/queue/game") sends the response to the user-specific queue /user/{username}/queue/game
    @MessageMapping("/start")
    @SendToUser("/queue/game")
    public String startGame(Principal principal) {

        matchmaker.addPlayerToQueue(principal.getName());
        System.out.println("User added to queue: " + principal.getName());

        String responseJson = String.format("{\"message\":\"Welcome to waiting queue, %s\"}", principal.getName());


        return responseJson;
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

