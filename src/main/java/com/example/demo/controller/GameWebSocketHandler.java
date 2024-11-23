package com.example.demo.controller;

import com.example.demo.service.GameMatchmaker;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class GameWebSocketHandler extends TextWebSocketHandler {

    private final GameMatchmaker matchmaker = new GameMatchmaker();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = getUsernameFromSession(session);
        if (username != null) {
            matchmaker.addPlayerToQueue(username, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages if needed
        System.out.println("Received message from " + session.getId() + ": " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Handle disconnection logic if needed
        System.out.println("Connection closed: " + session.getId());
    }

    private String getUsernameFromSession(WebSocketSession session) {
        // Extract the username from the session (e.g., query params, headers, etc.)
        // For example:
        return session.getUri().getQuery().replace("username=", "");
    }
}