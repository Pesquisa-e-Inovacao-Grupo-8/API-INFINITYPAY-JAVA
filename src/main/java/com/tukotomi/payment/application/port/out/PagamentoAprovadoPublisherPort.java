package com.tukotomi.payment.application.port.out;

public interface PagamentoAprovadoPublisherPort {
    void publicarPagamentoAprovado(String orderNsu, String status);
}