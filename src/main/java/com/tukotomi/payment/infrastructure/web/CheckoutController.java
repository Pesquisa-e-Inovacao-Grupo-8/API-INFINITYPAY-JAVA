package com.tukotomi.payment.infrastructure.web;

import com.tukotomi.payment.application.port.in.CreateCheckoutUseCase;
import com.tukotomi.payment.domain.model.CheckoutInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/flask-infinity-pay")
@CrossOrigin(origins = "*") // Habilita o CORS para o Frontend
public class CheckoutController {

    private final CreateCheckoutUseCase createCheckoutUseCase;

    public CheckoutController(CreateCheckoutUseCase createCheckoutUseCase) {
        this.createCheckoutUseCase = createCheckoutUseCase;
    }

    @PostMapping("/create-checkout")
    public ResponseEntity<CheckoutInfo> createCheckout(@RequestBody Map<String, String> payload) {
        // Pega o ID que o frontend enviou no JSON
        String agendamentoId = payload.get("id");

        // Trata caso o frontend envie como "idAgendamento" em vez de "id" (não lembro qual está)
        if (agendamentoId == null) {
            agendamentoId = payload.get("idAgendamento");
        }

        CheckoutInfo checkoutInfo = createCheckoutUseCase.execute(agendamentoId);

        return ResponseEntity.ok(checkoutInfo);
    }
}