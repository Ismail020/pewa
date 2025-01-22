package com.example.demo.controller;

import com.example.demo.service.GameMatchmaker;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Map;


@Controller
public class MatchmakingController {
    private final GameMatchmaker matchmaker;

    public MatchmakingController(GameMatchmaker matchmaker) {
        this.matchmaker = matchmaker;
    }

    @MessageMapping("/queue/enter")
    public void joinQueue(@Payload Principal principal, @Payload String message) {

        System.out.println("Message content: " + message);

        String currentUser = principal.getName();

        System.out.println(currentUser + " wants to join the queue!");
        matchmaker.addPlayerToQueue(currentUser);
    }

    @MessageMapping("/queue/leave")
    public void leaveQueue(Principal principal) {
        String currentUser = principal.getName();
        System.out.println(principal.getName() + "is leaving the queue");
        matchmaker.removePlayerFromQueue(currentUser);
    }

    @MessageMapping("/queue/challenge")
    @SendToUser("/queue/error")
    public String challengePlayer(@Payload String challenged, Principal principal) {
        String challenger = principal.getName();
        matchmaker.handleChallenge(challenger, challenged);
        return null; // Only sends error if challenge fails
    }
}
