package com.example.demo.config;

import com.example.demo.security.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
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
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

   @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
   }

   @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/game")
                .setAllowedOrigins("*")
                .addInterceptors(webSocketAuthInterceptor)
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        System.out.println("Principal assigned.");
                        return new Principal() {
                            @Override
                            public String getName() {
                                return (String) attributes.get("username");
                            }
                        };
                    }
                })
                .withSockJS();
   }
}
