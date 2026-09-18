package com.tukotomi.payment.domain.model;

public record Agendamento(
        String id,
        String status,
        Double valorTotal,
        Servico servico,
        Cliente cliente,
        String nomeClienteAvulso,
        String telefoneClienteAvulso
) {
    public record Servico(String nome) { }

    public record Cliente(Usuario usuario) { }

    public record Usuario(String nome, String email, String telefone) { }
}