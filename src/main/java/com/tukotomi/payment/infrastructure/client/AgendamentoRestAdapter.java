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
    private final String springApiKey;

    public AgendamentoRestAdapter(RestClient.Builder restClientBuilder,
                                  @Value("${spring.api.key}") String springApiKey,
                                  @Value("${api.core.url:http://localhost:8080}") String coreUrl) {
        // Configura a URL para o backend core
        this.restClient = restClientBuilder.baseUrl(coreUrl).build();
        this.springApiKey = springApiKey;
    }

    @Override
    public Agendamento findById(String id) {
        return restClient.get()
                .uri("/agendamentos/{id}", id)
                .header("API-KEY", springApiKey)
                .retrieve()
                .body(Agendamento.class);
    }

    @Override
    public void updateStatus(String id, String status) {
        restClient.patch()
                .uri("/agendamentos/{id}/pagamento", id) // Ajustar minha rota dps
                .header("API-KEY", springApiKey)
                .body(Map.of("status", status))
                .retrieve()
                .toBodilessEntity();
    }
}