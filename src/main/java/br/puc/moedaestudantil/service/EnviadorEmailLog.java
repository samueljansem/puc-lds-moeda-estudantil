package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.model.Notificacao;
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
    public void enviar(Notificacao n) {
        LOG.info("[EMAIL-SIM] to={} subject=\"{}\" code={}",
                n.getDestinatario(), n.getAssunto(), n.getCodigoReferencia());
    }
}
