package com.tukotomi.payment.application.port.in;

import java.util.Map;

public interface ProcessWebhookUseCase {
    void process(Map<String, Object> payload);
}