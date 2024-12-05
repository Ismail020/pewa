package com.example.demo.controller;

import com.example.demo.service.GameMatchmaker;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GameController {

    private final GameMatchmaker matchmaker;

    public GameController(GameMatchmaker matchmaker) {
        this.matchmaker = matchmaker;
    }

    @MessageMapping("/join")
    public void joinGame(Principal principal) {
        matchmaker.addPlayerToQueue(principal.getName());
    }
}

