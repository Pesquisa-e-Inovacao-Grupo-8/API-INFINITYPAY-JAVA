package com.tukotomi.payment.infrastructure.messaging;

import com.tukotomi.payment.application.port.out.PagamentoAprovadoPublisherPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitMQPagamentoPublisherAdapter implements PagamentoAprovadoPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQPagamentoPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicarPagamentoAprovado(String agendamentoId, String status) {
        Map<String, String> payload = new HashMap<>();
        payload.put("agendamentoId", agendamentoId);
        payload.put("status", status);

        // Dispara para o Exchange configurado com a routing key "pagamento.aprovado"
        rabbitTemplate.convertAndSend("pagamento.exchange", "pagamento.aprovado", payload);
    }
}