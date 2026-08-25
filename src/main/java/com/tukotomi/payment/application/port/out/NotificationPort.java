package com.tukotomi.payment.application.port.out;

public interface NotificationPort {
    void notifyPaymentConfirmed(String orderNsu);
}