package com.example.demo.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat/{gameId}")
    @SendTo("/topic/chat/{gameId}")
    public ChatMessageModel broadcastMessage(@DestinationVariable String gameId, ChatMessageModel message) {
        return message; // Broadcast the received message back to subscribers
    }
}
