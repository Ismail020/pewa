package com.example.demo.security;

import com.example.demo.service.JwtService;
import com.example.demo.user.CustomUserDetailsService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class WebsocketChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public WebsocketChannelInterceptor(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println("Message received: " + message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            System.out.println("Token received in Websocket.CONNECT: " + token);
            System.out.println("Headers: " + accessor.toNativeHeaderMap());


            if (token == null || !token.startsWith("Bearer ")) {
                //System.out.println("Missing or invalid token");
                throw new IllegalArgumentException("Invalid or missing Authorization header");
            }
            token = token.substring(7);
            //System.out.println("Trimmed token is " + token);

            try {
                String username = jwtService.extractUsername(token);
                System.out.println("Extracted username: " + username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (!jwtService.isTokenValid(token, userDetails)) {
                    throw new IllegalArgumentException("Token is invalid for user: " + username);
                }

                accessor.setUser(() -> username);
                System.out.println("User successfully authenticated: " + username);
                //gameMatchmaker.addPlayerToQueue(username);
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
                throw new IllegalArgumentException("Invalid token");
            }
        }
        return message;
    }
}