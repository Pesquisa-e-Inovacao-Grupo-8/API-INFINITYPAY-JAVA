package com.tukotomi.payment.application.port.in;

import com.tukotomi.payment.domain.model.CheckoutInfo;

public interface CreateCheckoutUseCase {
    CheckoutInfo execute(String agendamentoId);
}