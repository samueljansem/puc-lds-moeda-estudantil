package br.puc.moedaestudantil.messaging;

import br.puc.moedaestudantil.dao.NotificacaoDAO;
import br.puc.moedaestudantil.model.Notificacao;
import br.puc.moedaestudantil.model.StatusNotificacao;
import br.puc.moedaestudantil.service.EnviadorEmail;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.transaction.Transactional;

/**
 * Consumer da fila {@code notificacoes.email}: entrega por e-mail via
 * {@link EnviadorEmail} (Resend quando configurado; log simulado caso contrário).
 *
 * Idempotente: mensagens reentregues de notificação já {@code ENVIADA} viram
 * no-op (sem reenvio de e-mail); {@link NotificacaoDAO#marcarEnviada} é um
 * UPDATE condicional que protege contra corrida entre consumers.
 */
@RabbitListener
@Requires(notEnv = Environment.TEST)
public class ListenerEmail {

    private final NotificacaoDAO notificacaoDAO;
    private final EnviadorEmail enviadorEmail;

    public ListenerEmail(NotificacaoDAO notificacaoDAO, EnviadorEmail enviadorEmail) {
        this.notificacaoDAO = notificacaoDAO;
        this.enviadorEmail = enviadorEmail;
    }

    @Queue(AmqpTopologia.QUEUE_EMAIL)
    @Transactional
    public void onMessage(NotificacaoPublicada msg) {
        Notificacao n = notificacaoDAO.findById(msg.id())
                .orElseThrow(() -> new IllegalStateException("Notificacao não encontrada: " + msg.id()));

        if (n.getStatus() == StatusNotificacao.ENVIADA) {
            return; // reentrega duplicada: e-mail já saiu, só ack
        }

        enviadorEmail.enviar(n.getDestinatario(), n.getAssunto(), n.getCorpo());

        notificacaoDAO.marcarEnviada(msg.id());
    }
}
