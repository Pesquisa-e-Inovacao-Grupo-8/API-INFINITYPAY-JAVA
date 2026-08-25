package com.tukotomi.payment.domain.model;

public record CheckoutInfo(
        String orderNsu,
        String url
) {}