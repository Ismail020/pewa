package com.example.demo.config;

import com.example.demo.security.WebsocketChannelInterceptor;
import com.example.demo.service.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebsocketChannelInterceptor interceptor;
    private final JwtService jwtService;
    public WebSocketConfig(WebsocketChannelInterceptor interceptor, JwtService jwtService) {
        this.interceptor = interceptor;
        this.jwtService = jwtService;
    }


   @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
   }

   @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/game")
                .setAllowedOrigins("http://127.0.0.1:5173", "http://localhost:5173")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String, Object> attributes) {
                        String token = request.getHeaders().getFirst("Authorization");

                        if (token != null && token.startsWith("Bearer ")) {
                            token = token.substring(7); // Remove "Bearer " prefix
                            try {
                                // Use JwtService to validate and extract the username
                                String username = jwtService.extractUsername(token);
                                System.out.println("Username extracted from token: " + username);

                                if (username != null) {
                                    Principal principal = () -> username;
                                    attributes.put("principal", principal);
                                    System.out.println("Assigned Principal: " + username);
                                    return principal; // Return a Principal with the username
                                }
                            } catch (Exception e) {
                                System.out.println("Token validation failed: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Authorization header is missing or invalid");
                        }

                        System.out.println("No Principal assigned");
                        return null;
                    }
                });
   }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(interceptor);
    }

}
