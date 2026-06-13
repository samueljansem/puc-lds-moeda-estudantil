package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.model.Notificacao;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entrega real de e-mail via Resend (https://resend.com). Ativa apenas quando
 * a propriedade {@code resend.api-key} existe (via env var {@code RESEND_API_KEY});
 * sem ela, o contexto usa {@link EnviadorEmailLog}.
 *
 * Envia em duas partes: o corpo original como text/plain e a versão com a
 * moldura visual da Caderneta ({@link MolduraEmailHtml}) como text/html.
 */
@Singleton
@Requires(property = "resend.api-key")
public class EnviadorEmailResend implements EnviadorEmail {

    private static final Logger LOG = LoggerFactory.getLogger(EnviadorEmailResend.class);

    private final Resend resend;
    private final String remetente;

    public EnviadorEmailResend(@Property(name = "resend.api-key") String apiKey,
                               @Property(name = "resend.from") String remetente) {
        this.resend = new Resend(apiKey);
        this.remetente = remetente;
    }

    @Override
    public void enviar(Notificacao n) {
        CreateEmailOptions email = CreateEmailOptions.builder()
                .from(remetente)
                .to(n.getDestinatario())
                .subject(n.getAssunto())
                .text(n.getCorpo())
                .html(MolduraEmailHtml.render(n.getAssunto(), n.getCorpo(), n.getCodigoReferencia()))
                .build();
        try {
            String id = resend.emails().send(email).getId();
            LOG.info("[EMAIL] enviado via Resend: to={} subject=\"{}\" id={}",
                    n.getDestinatario(), n.getAssunto(), id);
        } catch (ResendException e) {
            // Unchecked pra estourar no listener: nack e reentrega pelo broker.
            throw new IllegalStateException("Falha ao enviar e-mail via Resend para " + n.getDestinatario(), e);
        }
    }
}
