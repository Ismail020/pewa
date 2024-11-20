package com.example.demo.security;

import com.example.demo.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component

public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public WebSocketAuthInterceptor(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String token = request.getHeaders().getFirst("Authorization");

        if (token == null || token.isEmpty()) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7); //Remove 'Bearer' prefix

            String username = jwtService.extractUsername(token);
            System.out.println("Username extracted: " + username);
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, userDetails)) {
                    attributes.put("username", username);
                    return true;
                } else {
                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    return false;
                }
            }
        }
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return false;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
    }

}
