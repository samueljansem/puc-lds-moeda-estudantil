package br.puc.moedaestudantil.service;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modo simulado: sem {@code RESEND_API_KEY} no ambiente, apenas registra o
 * e-mail em log — comportamento original do lab, útil em dev sem credenciais.
 */
@Singleton
@Requires(missingProperty = "resend.api-key")
public class EnviadorEmailLog implements EnviadorEmail {

    private static final Logger LOG = LoggerFactory.getLogger(EnviadorEmailLog.class);

    @Override
    public void enviar(String para, String assunto, String corpo) {
        LOG.info("[EMAIL-SIM] to={} subject=\"{}\"", para, assunto);
    }
}
