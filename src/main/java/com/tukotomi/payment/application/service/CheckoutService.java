package com.tukotomi.payment.application.service;

import com.tukotomi.payment.application.port.in.CreateCheckoutUseCase;
import com.tukotomi.payment.application.port.out.AgendamentoClientPort;
import com.tukotomi.payment.application.port.out.PaymentGatewayPort;
import com.tukotomi.payment.domain.model.Agendamento;
import com.tukotomi.payment.domain.model.CheckoutInfo;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService implements CreateCheckoutUseCase {

    private final AgendamentoClientPort agendamentoPort;
    private final PaymentGatewayPort paymentGateway;

    public CheckoutService(AgendamentoClientPort agendamentoPort, PaymentGatewayPort paymentGateway) {
        this.agendamentoPort = agendamentoPort;
        this.paymentGateway = paymentGateway;
    }

    @Override
    public CheckoutInfo execute(String agendamentoId) {
        // Busca se o agendamento
        Agendamento agendamento = agendamentoPort.findById(agendamentoId);

        if (agendamento == null) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }

        // Gera e retorna o link de pagamento na Infinity Pay
        return paymentGateway.generateCheckoutLink(agendamento);
    }
}