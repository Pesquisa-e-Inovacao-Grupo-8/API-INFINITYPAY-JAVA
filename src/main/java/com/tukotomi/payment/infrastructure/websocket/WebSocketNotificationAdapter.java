package com.tukotomi.payment.infrastructure.websocket;

import com.tukotomi.payment.application.port.out.NotificationPort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebSocketNotificationAdapter implements NotificationPort {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void notifyPaymentConfirmed(String orderNsu) {
        // Cria o payload que o frontend está esperando receber
        Map<String, String> payload = Map.of(
                "order_nsu", orderNsu,
                "status", "CONFIRMADO"
        );

        // Envia a mensagem para todos os clientes conectados no canal "/topic/pagamentos"
        messagingTemplate.convertAndSend("/topic/pagamentos", payload);

        System.out.println("Disparado WebSocket de confirmação para o NSU: " + orderNsu);
    }
}