package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    /**
     * Essentially just the routing for the different handlers.
     * @param registry registry that helps you map websocket handlers to urls.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GameWebSocketHandler(), "/ws/game")
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOrigins("*"); // origins (connections) allowed from anywhere for now
        registry.addHandler(new IngameChatWebSocketHandler(), "/ws/ingame_chat").setAllowedOrigins("*"); // origins (connections) allowed from anywhere for now
        //optional to introduce new feature?
        //registry.addHandler(new GeneralChatWebSocketHandler(), "/ws/general_chat").setAllowedOrigins("*"); // origins (connections) allowed from anywhere for now
    }
}
