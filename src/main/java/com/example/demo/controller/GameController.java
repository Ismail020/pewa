package com.example.demo.controller;

import com.example.demo.service.GameMatchmaker;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

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

        return "Welcome to waiting queue, " + principal.getName();
    }

}

