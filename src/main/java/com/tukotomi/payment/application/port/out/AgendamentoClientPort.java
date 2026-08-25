package com.tukotomi.payment.application.port.out;

import com.tukotomi.payment.domain.model.Agendamento;

public interface AgendamentoClientPort {
    Agendamento findById(String id);
    void updateStatus(String id, String status);
}