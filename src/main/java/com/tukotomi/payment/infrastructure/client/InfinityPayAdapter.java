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
    private final String merchantHandle;

    public InfinityPayAdapter(@Value("${infinity.pay.handle}") String merchantHandle) {
        this.restClient = RestClient.builder().baseUrl("https://api.infinitepay.io").build();
        this.merchantHandle = merchantHandle;
    }

    @Override
    public CheckoutInfo generateCheckoutLink(Agendamento agendamento) {
        // Montamos o payload usando o seu handle
        Map<String, Object> payload = Map.of(
                "handle", merchantHandle,
                "order_nsu", agendamento.id(),
                "items", List.of(Map.of(
                        "quantity", 1,
                        "price", 10000, // Lembrando que na Infinity Pay geralmente o valor é em centavos (10000 = R$ 100,00)
                        "description", "Serviço Renata Tukotomi"
                )),
                "customer", Map.of(
                        "name", "Cliente Tukotomi",
                        "email", "cliente@tukotomi.com",
                        "phone_number", "11999999999"
                )
        );

        // Disparamos a requisição sem o .header("Authorization")
        Map response = restClient.post()
                .uri("/invoices/public/checkout/links")
                .body(payload)
                .retrieve()
                .body(Map.class);

        return new CheckoutInfo(agendamento.id(), (String) response.get("url"));
    }
}