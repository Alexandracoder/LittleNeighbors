package com.alexandracoder.littleneighbors.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Antes esta lista estaba hardcodeada (localhost + el viejo dominio de
    // onrender) y no incluía littleneighbors.es, así que el chat y las
    // notificaciones en tiempo real se rompían silenciosamente en el
    // dominio nuevo. Ahora lee la misma variable ALLOWED_ORIGINS que ya
    // usa SecurityConfig para CORS, para no tener que mantener la lista
    // en dos sitios distintos.
    @Value("${ALLOWED_ORIGINS:http://localhost:5173,http://localhost:5174}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-little-neighbors")
                .setAllowedOrigins(allowedOrigins.split(","))
                .withSockJS();
    }
}