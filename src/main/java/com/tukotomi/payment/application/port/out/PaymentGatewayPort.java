package com.tukotomi.payment.application.port.out;

import com.tukotomi.payment.domain.model.Agendamento;
import com.tukotomi.payment.domain.model.CheckoutInfo;

public interface PaymentGatewayPort {
    CheckoutInfo generateCheckoutLink(Agendamento agendamento);
}