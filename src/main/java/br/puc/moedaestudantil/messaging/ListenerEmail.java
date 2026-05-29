package br.puc.moedaestudantil.messaging;

import br.puc.moedaestudantil.dao.NotificacaoDAO;
import br.puc.moedaestudantil.model.Notificacao;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumer da fila {@code notificacoes.email}: simula a entrega por e-mail.
 *
 * Side-effect real seria um {@code MailSender}; aqui é log estruturado.
 * Idempotente por {@link NotificacaoDAO#marcarEnviada} (UPDATE condicional).
 */
@RabbitListener
@Requires(notEnv = Environment.TEST)
public class ListenerEmail {

    private static final Logger LOG = LoggerFactory.getLogger(ListenerEmail.class);

    private final NotificacaoDAO notificacaoDAO;

    public ListenerEmail(NotificacaoDAO notificacaoDAO) {
        this.notificacaoDAO = notificacaoDAO;
    }

    @Queue(AmqpTopologia.QUEUE_EMAIL)
    @Transactional
    public void onMessage(NotificacaoPublicada msg) {
        Notificacao n = notificacaoDAO.findById(msg.id())
                .orElseThrow(() -> new IllegalStateException("Notificacao não encontrada: " + msg.id()));

        LOG.info("[EMAIL-SIM] to={} subject=\"{}\" code={}",
                n.getDestinatario(), n.getAssunto(), n.getCodigoReferencia());

        notificacaoDAO.marcarEnviada(msg.id());
    }
}
