package br.puc.moedaestudantil.service;

import java.time.LocalDateTime;

public record LinhaExtrato(
        LocalDateTime data,
        String tipo,
        String descricao,
        int valor,
        String referencia
) {
}
