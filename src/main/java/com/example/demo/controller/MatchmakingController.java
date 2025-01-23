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

    /**
     * Handles a player's request to join the matchmaking queue.
     *
     * @param principal the principal representing the authenticated player
     * @param message   the payload message sent by the client
     */
    @MessageMapping("/queue/enter")
    public void joinQueue(@Payload Principal principal, @Payload String message) {

        System.out.println("Message content: " + message);

        String currentUser = principal.getName();

        System.out.println(currentUser + " wants to join the queue!");
        matchmaker.addPlayerToQueue(currentUser);
    }

    /**
     * Handles a player's request to leave the matchmaking queue.
     *
     * @param principal the principal representing the authenticated player
     */
    @MessageMapping("/queue/leave")
    public void leaveQueue(Principal principal) {
        String currentUser = principal.getName();
        System.out.println(principal.getName() + "is leaving the queue");
        matchmaker.removePlayerFromQueue(currentUser);
    }

    /**
     * Handles a player's request to challenge another player in the queue.
     *
     * @param challenged the username of the player being challenged
     * @param principal  the principal representing the authenticated challenger
     * @return null to indicate that the only response is an error if the challenge fails
     */
    @MessageMapping("/queue/challenge")
    @SendToUser("/queue/error")
    public void challengePlayer(@Payload String challenged, Principal principal) {
        // Remove double quotes from the start and end of the string
        challenged = challenged.replaceAll("^\"|\"$", "");

        System.out.println("awesome, " + principal.getName() + " challenged " + challenged);
        String challenger = principal.getName();
        matchmaker.handleChallenge(challenger, challenged);
    }

}
