package com.tukotomi.payment.application.service;

import com.tukotomi.payment.application.port.out.AgendamentoClientPort;
import com.tukotomi.payment.application.port.out.PaymentGatewayPort;
import com.tukotomi.payment.domain.model.Agendamento;
import com.tukotomi.payment.domain.model.CheckoutInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Dizemos ao JUnit para usar o Mockito neste teste
@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    // @Mock cria "dublês" das nossas interfaces. Eles não fazem HTTP de verdade.
    @Mock
    private AgendamentoClientPort agendamentoPort;

    @Mock
    private PaymentGatewayPort paymentGateway;

    // @InjectMocks injeta os dublês dentro do nosso CheckoutService real
    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    @DisplayName("Deve gerar link de checkout com sucesso quando o agendamento existir")
    void deveGerarCheckoutComSucesso() {
        // 1. ARRANGE (Preparação)
        String idTeste = "123";
        Agendamento agendamentoMock = new Agendamento(idTeste, "PENDENTE");
        CheckoutInfo checkoutEsperado = new CheckoutInfo(idTeste, "https://link-falso.com");

        // Ensinamos o dublê: "Quando pedirem o ID 123, devolva o agendamentoMock"
        when(agendamentoPort.findById(idTeste)).thenReturn(agendamentoMock);

        // Ensinamos o outro dublê: "Quando pedirem para gerar o link desse agendamento, devolva o checkoutEsperado"
        when(paymentGateway.generateCheckoutLink(agendamentoMock)).thenReturn(checkoutEsperado);

        // 2. ACT (Ação) -> Executamos a lógica real
        CheckoutInfo resultado = checkoutService.execute(idTeste);

        // 3. ASSERT (Verificação) -> Garantimos que a lógica funcionou
        assertNotNull(resultado);
        assertEquals("https://link-falso.com", resultado.url());

        // Verifica se a porta foi chamada exatamente 1 vez
        verify(agendamentoPort, times(1)).findById(idTeste);
        verify(paymentGateway, times(1)).generateCheckoutLink(agendamentoMock);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o agendamento não for encontrado")
    void deveLancarExcecaoQuandoAgendamentoNaoExistir() {
        // 1. ARRANGE
        String idTeste = "999";

        // Ensinamos o dublê a devolver nulo (simulando que não achou no banco)
        when(agendamentoPort.findById(idTeste)).thenReturn(null);

        // 2 & 3. ACT & ASSERT
        // Verifica se a execução da lógica lança a exceção esperada
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkoutService.execute(idTeste)
        );

        assertEquals("Agendamento não encontrado", exception.getMessage());

        // Garante que o gateway de pagamento NUNCA foi chamado, afinal, deu erro antes
        verify(paymentGateway, never()).generateCheckoutLink(any());
    }
}