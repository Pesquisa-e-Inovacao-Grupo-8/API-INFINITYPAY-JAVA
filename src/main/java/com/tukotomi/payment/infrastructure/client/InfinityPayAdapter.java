package com.tukotomi.payment.infrastructure.client;

import com.tukotomi.payment.application.port.out.PaymentGatewayPort;
import com.tukotomi.payment.domain.model.Agendamento;
import com.tukotomi.payment.domain.model.CheckoutInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class InfinityPayAdapter implements PaymentGatewayPort {

    private final RestClient restClient;
    private final String merchantHandle;
        private final String webhookUrl;

        public InfinityPayAdapter(@Value("${infinity.pay.handle}") String merchantHandle,
                                                          @Value("${infinity.pay.webhook-url}") String webhookUrl) {
        this.restClient = RestClient.builder().baseUrl("https://api.infinitepay.io").build();
        this.merchantHandle = merchantHandle;
                this.webhookUrl = webhookUrl;
    }

    @Override
    public CheckoutInfo generateCheckoutLink(Agendamento agendamento) {
        if (agendamento.valorTotal() == null || agendamento.valorTotal() <= 0) {
            throw new IllegalArgumentException("O agendamento não possui um valor válido para pagamento");
        }

        int priceInCents = (int) Math.round(agendamento.valorTotal() * 100);
        Agendamento.Usuario usuario = agendamento.cliente() == null
                ? null
                : agendamento.cliente().usuario();

        String customerName = firstNonBlank(
                usuario == null ? null : usuario.nome(),
                agendamento.nomeClienteAvulso(),
                "Cliente Tukotomi");
        String customerEmail = firstNonBlank(
                usuario == null ? null : usuario.email(),
                "cliente@tukotomi.com");
        String customerPhone = firstNonBlank(
                usuario == null ? null : usuario.telefone(),
                agendamento.telefoneClienteAvulso(),
                "00000000000");
        String description = firstNonBlank(
                agendamento.servico() == null ? null : agendamento.servico().nome(),
                "Serviço Tukotomi");

        Map<String, Object> payload = Map.of(
                "handle", merchantHandle,
                "order_nsu", agendamento.id(),
                "webhook_url", webhookUrl,
                "items", List.of(Map.of(
                        "quantity", 1,
                        "price", priceInCents,
                        "description", description
                )),
                "customer", Map.of(
                        "name", customerName,
                        "email", customerEmail,
                        "phone_number", customerPhone
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

        private String firstNonBlank(String... values) {
                for (String value : values) {
                        if (Objects.nonNull(value) && !value.isBlank()) {
                                return value;
                        }
                }
                return "Não informado";
        }
}