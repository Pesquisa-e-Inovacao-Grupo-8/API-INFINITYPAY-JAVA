package com.tukotomi.payment.infrastructure.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita um canal chamado "/topic" para onde vão as mensagens de pagamento
        config.enableSimpleBroker("/topic");

        // Prefixo para mensagens enviadas do cliente para o servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Rota que o Frontend vai usar para se conectar ao WebSocket do Java
        registry.addEndpoint("/ws-payment")
                .setAllowedOriginPatterns("*") // CORS
                .withSockJS(); // Fallback caso o navegador não suporte WebSocket nativo
    }
}