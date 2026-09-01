package com.tukotomi.payment.application.service;

import com.tukotomi.payment.application.port.out.AgendamentoClientPort;
import com.tukotomi.payment.application.port.out.NotificationPort;
import com.tukotomi.payment.domain.model.Agendamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    private AgendamentoClientPort agendamentoPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private WebhookService webhookService;

    @Test
    @DisplayName("Deve atualizar status e notificar quando receber webhook de um agendamento válido")
    void deveProcessarWebhookComSucesso() {
        // 1. ARRANGE
        String orderNsu = "123";
        Map<String, Object> payload = Map.of("order_nsu", orderNsu);
        Agendamento agendamentoMock = new Agendamento(orderNsu, "PENDENTE");

        // Ensinamos o dublê a encontrar o agendamento
        when(agendamentoPort.findById(orderNsu)).thenReturn(agendamentoMock);

        // 2. ACT
        webhookService.process(payload);

        // 3. ASSERT
        // Verificamos se a porta de atualizar status foi chamada exatamente 1 vez com os dados certos
        verify(agendamentoPort, times(1)).updateStatus(orderNsu, "CONFIRMADO");

        // Verificamos se o WebSocket foi disparado
        verify(notificationPort, times(1)).notifyPaymentConfirmed(orderNsu);
    }

    @Test
    @DisplayName("Não deve fazer nada se o agendamento do webhook não for encontrado")
    void naoDeveProcessarSeAgendamentoInvalido() {
        // 1. ARRANGE
        String orderNsu = "999";
        Map<String, Object> payload = Map.of("order_nsu", orderNsu);

        // Simulamos o banco de dados não encontrando o agendamento
        when(agendamentoPort.findById(orderNsu)).thenReturn(null);

        // 2. ACT
        webhookService.process(payload);

        // 3. ASSERT
        // Garante que os métodos de atualização e notificação NUNCA foram chamados
        verify(agendamentoPort, never()).updateStatus(anyString(), anyString());
        verify(notificationPort, never()).notifyPaymentConfirmed(anyString());
    }
}