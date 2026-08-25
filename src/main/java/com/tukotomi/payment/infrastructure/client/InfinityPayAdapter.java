package com.tukotomi.payment.infrastructure.client;

import com.tukotomi.payment.application.port.out.PaymentGatewayPort;
import com.tukotomi.payment.domain.model.Agendamento;
import com.tukotomi.payment.domain.model.CheckoutInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class InfinityPayAdapter implements PaymentGatewayPort {

    private final RestClient restClient;
    private final String apiKey;

    public InfinityPayAdapter(RestClient.Builder restClientBuilder,
                              @Value("${infinity.api.key:chave_aqui}") String apiKey) { //colocar a chave
        // Configura a URL da Infinity Pay
        this.restClient = restClientBuilder.baseUrl("https://api.infinitepay.io").build();
        this.apiKey = apiKey;
    }

    @Override
    public CheckoutInfo generateCheckoutLink(Agendamento agendamento) {
        // Monta o payload
        Map<String, Object> payload = Map.of(
                "handle", "alexsander-torres",
                "order_nsu", agendamento.id(),
                "items", List.of(Map.of(
                        "quantity", 1,
                        "price", 10000,
                        "description", "Serviço Renata Tukotomi"
                )),
                "customer", Map.of(
                        "name", "Alex",
                        "email", "Alex@gmail.com",
                        "phone_number", "11999999999"
                )
        );

        // POST e mapeia a resposta
        Map response = restClient.post()
                .uri("/invoices/public/checkout/links")
                .header("Authorization", "Bearer " + apiKey)
                .body(payload)
                .retrieve()
                .body(Map.class);

        // Retorna model de domínio com a URL extraída
        return new CheckoutInfo(agendamento.id(), (String) response.get("url"));
    }
}