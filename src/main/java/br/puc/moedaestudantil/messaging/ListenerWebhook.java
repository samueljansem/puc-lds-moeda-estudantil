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
 * Consumer da fila {@code notificacoes.webhook}: simula POST para um sistema
 * externo da empresa parceira. Side-effect real seria um {@code HttpClient};
 * aqui é log estruturado.
 *
 * Idempotente por {@link NotificacaoDAO#marcarEnviada} (UPDATE condicional) —
 * se o listener de e-mail já marcou ENVIADA, este vira no-op no DB.
 */
@RabbitListener
@Requires(notEnv = Environment.TEST)
public class ListenerWebhook {

    private static final Logger LOG = LoggerFactory.getLogger(ListenerWebhook.class);

    private final NotificacaoDAO notificacaoDAO;

    public ListenerWebhook(NotificacaoDAO notificacaoDAO) {
        this.notificacaoDAO = notificacaoDAO;
    }

    @Queue(AmqpTopologia.QUEUE_WEBHOOK)
    @Transactional
    public void onMessage(NotificacaoPublicada msg) {
        Notificacao n = notificacaoDAO.findById(msg.id())
                .orElseThrow(() -> new IllegalStateException("Notificacao não encontrada: " + msg.id()));

        LOG.info("[WEBHOOK-SIM] POST /parceira/notify body=(id:{}, codigo:\"{}\", assunto:\"{}\")",
                n.getId(), n.getCodigoReferencia(), n.getAssunto());

        notificacaoDAO.marcarEnviada(msg.id());
    }
}
