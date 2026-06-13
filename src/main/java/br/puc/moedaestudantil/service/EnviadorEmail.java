package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.model.Notificacao;

/**
 * Porta de saída para entrega de e-mail. Implementações: {@link EnviadorEmailResend}
 * (envio real, ativo quando {@code RESEND_API_KEY} está definida) e
 * {@link EnviadorEmailLog} (modo simulado, default em dev/teste).
 */
public interface EnviadorEmail {

    void enviar(Notificacao notificacao);
}
