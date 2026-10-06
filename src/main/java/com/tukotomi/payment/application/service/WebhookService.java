package com.tukotomi.payment.application.service;

import com.tukotomi.payment.application.port.in.ProcessWebhookUseCase;
import com.tukotomi.payment.application.port.out.AgendamentoClientPort;
import com.tukotomi.payment.application.port.out.NotificationPort;
import com.tukotomi.payment.application.port.out.PagamentoAprovadoPublisherPort;
import com.tukotomi.payment.domain.model.Agendamento;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebhookService implements ProcessWebhookUseCase {

    private final AgendamentoClientPort agendamentoPort;
    private final NotificationPort notificationPort;
    private final PagamentoAprovadoPublisherPort pagamentoPublisherPort;

    public WebhookService(
            AgendamentoClientPort agendamentoPort,
            NotificationPort notificationPort,
            PagamentoAprovadoPublisherPort pagamentoPublisherPort
    ) {
        this.agendamentoPort = agendamentoPort;
        this.notificationPort = notificationPort;
        this.pagamentoPublisherPort = pagamentoPublisherPort;
    }

    @Override
    public void process(Map<String, Object> payload) {
        // Extrai o ID do agendamento (NSU)
        String orderNsu = (String) payload.get("order_nsu");

        // Verifica se o agendamento existe
        Agendamento agendamento = agendamentoPort.findById(orderNsu);
        if (agendamento != null) {
            // 1. Atualização direta/local
            agendamentoPort.updateStatus(orderNsu, "CONFIRMADO");

            // 2. Disparo do WebSocket para o Frontend
            notificationPort.notifyPaymentConfirmed(orderNsu);

            // 3. Disparo da mensagem assíncrona para a fila do RabbitMQ
            pagamentoPublisherPort.publicarPagamentoAprovado(orderNsu, "PAGO");
        }
    }
}