package br.puc.moedaestudantil.messaging;

import br.puc.moedaestudantil.model.Notificacao;
import br.puc.moedaestudantil.model.StatusNotificacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListenerWebhookTest {

    @Test
    void entregaMarcaENVIADA() {
        var dao = new FakeNotificacaoDAO(List.of(pendente(50L)));

        new ListenerWebhook(dao).onMessage(new NotificacaoPublicada(50L));

        assertEquals(StatusNotificacao.ENVIADA, dao.findById(50L).orElseThrow().getStatus());
    }

    @Test
    void seEmailJaMarcouENVIADA_webhookEhNoOp() {
        var n = pendente(51L);
        var dao = new FakeNotificacaoDAO(List.of(n));
        // Simula que o ListenerEmail rodou primeiro:
        new ListenerEmail(dao, notificacao -> { }).onMessage(new NotificacaoPublicada(51L));

        // Webhook recebe a mesma mensagem (fanout) — UPDATE retorna 0 linhas, sem throw.
        new ListenerWebhook(dao).onMessage(new NotificacaoPublicada(51L));

        assertEquals(StatusNotificacao.ENVIADA, dao.findById(51L).orElseThrow().getStatus());
    }

    private static Notificacao pendente(long id) {
        Notificacao n = new Notificacao(
                "empresa@x.com", "Resgate confirmado", "Corpo",
                StatusNotificacao.PENDENTE, "cod-" + id, LocalDateTime.now());
        n.setId(id);
        return n;
    }
}
