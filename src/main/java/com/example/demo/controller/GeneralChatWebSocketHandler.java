package com.example.demo.controller;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GeneralChatWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>(); //not a normal hashmap for thread safety, in websockets every websession's requests/message handling is on a different thread.


    /**
     * Functionality for after a connection has been established. Sends welcoming message to client's newly connected session.
     * @param session websocket session the client is connected to
     * @throws Exception catches all exceptions and throws them.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception { //throw exception is part of the signature of this, handletextmessage() and afterconnectionclosed(). catches all exceptions thrown.
        sessions.put(session.getId(), session);
        session.sendMessage(new TextMessage("Welcome to the zeeslag general-chat WebSocket server!")); // sent to client's session
    }

    /**
     * Functionality for sending messages to the other clients such as turns.
     * @param session websocket session the client is connected to
     * @param message message sent by the server to the client.
     * @throws Exception catches all exceptions and throws them.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        //sends message to all connected clients (including self)
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    /**
     * handles session after it was closed.
     * @param session websocket session the client is connected to
     * @param status status of the (attempted) closed session.
     * @throws Exception catches all exceptions and throws them.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //ensure session is closed
        if (session.isOpen()) {
            session.close();
        }

        // Log the closure status
        System.out.println("Connection closed. Code: " + status.getCode() + " Reason: " + status.getReason());

        //remove session from active sessions map
        sessions.remove(session.getId());

    }
}
