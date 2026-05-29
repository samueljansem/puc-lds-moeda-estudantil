package br.puc.moedaestudantil.messaging;

import br.puc.moedaestudantil.dao.NotificacaoDAO;
import br.puc.moedaestudantil.model.Notificacao;
import br.puc.moedaestudantil.model.StatusNotificacao;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Relay do Transactional Outbox: a cada 2s lê linhas {@code PENDENTE} mais
 * antigas e publica no exchange {@code notificacoes}. Se o broker estiver
 * indisponível, dá log e sai — o próximo tick republica (retry built-in).
 *
 * O tick usa {@code fixedDelay} (não {@code fixedRate}) — espera 2s APÓS o
 * término do tick anterior, evitando overlap em execuções longas.
 */
@Singleton
@Requires(notEnv = Environment.TEST) // listeners diretos nos testes unitários
public class DrainadorNotificacoes {

    private static final Logger LOG = LoggerFactory.getLogger(DrainadorNotificacoes.class);

    private final NotificacaoDAO notificacaoDAO;
    private final PublisherNotificacao publisher;

    public DrainadorNotificacoes(NotificacaoDAO notificacaoDAO,
                                 PublisherNotificacao publisher) {
        this.notificacaoDAO = notificacaoDAO;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelay = "2s")
    public void drenar() {
        List<Notificacao> pendentes =
                notificacaoDAO.findFirst50ByStatusOrderByCriadaEmAsc(StatusNotificacao.PENDENTE);
        if (pendentes.isEmpty()) {
            return;
        }
        LOG.debug("Outbox: {} notificação(ões) pendente(s) — publicando…", pendentes.size());
        for (Notificacao n : pendentes) {
            try {
                publisher.publicar(new NotificacaoPublicada(n.getId()));
            } catch (Exception e) {
                LOG.warn("Broker indisponível ao publicar id={}. Republicação no próximo tick. ({})",
                        n.getId(), e.getMessage());
                return; // interrompe a iteração; tenta de novo em 2s
            }
        }
    }
}
