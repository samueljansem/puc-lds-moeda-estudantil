package br.puc.moedaestudantil.messaging;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Payload das mensagens da fila {@code notificacoes}.
 * Carrega apenas o id — o consumer lê a linha atual do banco (fonte da verdade).
 */
@Serdeable
public record NotificacaoPublicada(Long id) {
}
