package com.tukotomi.payment.infrastructure.web;

import com.tukotomi.payment.application.port.in.ProcessWebhookUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final ProcessWebhookUseCase processWebhookUseCase;

    public WebhookController(ProcessWebhookUseCase processWebhookUseCase) {
        this.processWebhookUseCase = processWebhookUseCase;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("=+".repeat(15));
        System.out.println("Evento recebido da Infinity Pay: " + payload);
        System.out.println("=+".repeat(15));

        // Envia o JSON recebido para o Caso de Uso processar as regras
        processWebhookUseCase.process(payload);

        // A Infinity Pay exige que retornemos status 200 OK
        return ResponseEntity.ok("ok");
    }
}