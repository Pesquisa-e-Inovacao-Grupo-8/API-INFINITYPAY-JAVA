package com.tukotomi.payment.application.service;

import com.tukotomi.payment.application.port.in.ProcessWebhookUseCase;
import com.tukotomi.payment.application.port.out.AgendamentoClientPort;
import com.tukotomi.payment.application.port.out.NotificationPort;
import com.tukotomi.payment.domain.model.Agendamento;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebhookService implements ProcessWebhookUseCase {

    private final AgendamentoClientPort agendamentoPort;
    private final NotificationPort notificationPort;

    public WebhookService(AgendamentoClientPort agendamentoPort, NotificationPort notificationPort) {
        this.agendamentoPort = agendamentoPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void process(Map<String, Object> payload) {
        // Extrai o ID do agendamento (NSU) que a Infinity Pay envia no webhook
        String orderNsu = (String) payload.get("order_nsu");

        // Verifica se o agendamento é válido
        Agendamento agendamento = agendamentoPort.findById(orderNsu);
        if (agendamento != null) {
            // Atualiza o status via API Core
            agendamentoPort.updateStatus(orderNsu, "CONFIRMADO");

            // Dispara o WebSocket para o Frontend
            notificationPort.notifyPaymentConfirmed(orderNsu);
        }
    }
}