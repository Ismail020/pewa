package com.example.demo.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final GameMatchmaker matchmaker = new GameMatchmaker();
    /**
     * Functionality for after a connection has been established. Welcomes player to the queue.
     * @param session websocket session the client is connected to
     * @throws Exception catches all exceptions and throws them.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");

        if (username == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        System.out.println("New connection established: " + session.getId());
        matchmaker.addPlayerToQueue(session);  // Add player to matchmaking queue
    }

    private String getTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("token=")) {
            return query.split("token=")[1];
        }
        return null;
    }

    /**
     * Functionality for sending messages to the other clients such as turns.
     * @param session websocket session the client is connected to
     * @param message message sent by the server to the client.
     * @throws Exception catches all exceptions and throws them.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Handling websocket message");
        //sends message to all connected clients (except sender)
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen() && !s.equals(session)) {
                s.sendMessage(new TextMessage("Message from " + session.getId() + ": " + message.getPayload()));
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
