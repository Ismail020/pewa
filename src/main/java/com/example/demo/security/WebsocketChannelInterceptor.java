package com.example.demo.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebsocketChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println("Message received: " + message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            System.out.println("Token received in Websocket.CONNECT: " + token);
            System.out.println("Headers: " + accessor.toNativeHeaderMap());


            if (token == null || !token.startsWith("Bearer ")) {
                System.out.println("Missing or invalid token");
                throw new IllegalArgumentException("Invalid or missing Authorization header");
            }
            token = token.substring(7);
            System.out.println("Trimmed token is " + token);

            if (!isTokenValid(token)) {
                throw new IllegalArgumentException("Invalid token " + token);
            }
            System.out.println("Token is valid");
        }
        return message;
    }

    private boolean isTokenValid(String token) {
        // todo
        return true;
    }
}