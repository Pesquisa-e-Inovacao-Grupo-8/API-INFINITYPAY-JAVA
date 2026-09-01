package com.tukotomi.payment.infrastructure.client;

import com.tukotomi.payment.application.port.out.AgendamentoClientPort;
import com.tukotomi.payment.domain.model.Agendamento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AgendamentoRestAdapter implements AgendamentoClientPort {

    private final RestClient restClient;

    public AgendamentoRestAdapter(@Value("${spring.api.key}") String springApiKey,
                                  @Value("${api.core.url:http://localhost:8080}") String coreUrl) {
        // Configuramos a URL base e o cabeçalho padrão UMA ÚNICA VEZ
        this.restClient = RestClient.builder()
                .baseUrl(coreUrl)
                .defaultHeader("API-KEY", springApiKey) // A mágica acontece aqui!
                .build();
    }

    @Override
    public Agendamento findById(String id) {
        return restClient.get()
                .uri("/agendamentos/{id}", id)
                .retrieve()
                .body(Agendamento.class);
    }

    @Override
    public void updateStatus(String id, String status) {
        restClient.patch()
                .uri("/agendamentos/{id}/pagamento", id)
                .body(Map.of("status", status))
                .retrieve()
                .toBodilessEntity();
    }
}