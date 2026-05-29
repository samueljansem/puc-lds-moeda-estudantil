package br.puc.moedaestudantil.messaging;

import br.puc.moedaestudantil.model.Notificacao;
import br.puc.moedaestudantil.model.StatusNotificacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DrainadorNotificacoesTest {

    @Test
    void semPendentes_naoPublica() {
        var dao = new FakeNotificacaoDAO(List.of());
        var publisher = new FakePublisher();

        new DrainadorNotificacoes(dao, publisher).drenar();

        assertTrue(publisher.publicados.isEmpty(), "nenhuma publicação quando fila local está vazia");
    }

    @Test
    void comTresPendentes_publicaTodasNaOrdem() {
        var n1 = notif(10L);
        var n2 = notif(11L);
        var n3 = notif(12L);
        var dao = new FakeNotificacaoDAO(List.of(n1, n2, n3));
        var publisher = new FakePublisher();

        new DrainadorNotificacoes(dao, publisher).drenar();

        assertEquals(List.of(10L, 11L, 12L), publisher.publicados);
    }

    @Test
    void brokerIndisponivel_interrompeIteracaoSemPropagar() {
        var dao = new FakeNotificacaoDAO(List.of(notif(20L), notif(21L), notif(22L)));
        var publisher = new FakePublisher();
        publisher.lancarAPartirDoId = 21L; // segunda publicação explode

        new DrainadorNotificacoes(dao, publisher).drenar();

        // Só a primeira foi entregue ao publisher; restante fica pro próximo tick.
        assertEquals(List.of(20L), publisher.publicados);
    }

    private static Notificacao notif(long id) {
        Notificacao n = new Notificacao(
                "dest@x.com", "Assunto", "Corpo",
                StatusNotificacao.PENDENTE, "cod-" + id, LocalDateTime.now());
        n.setId(id);
        return n;
    }

    /** Fake do publisher que registra os ids publicados e pode simular falha. */
    static class FakePublisher implements PublisherNotificacao {
        final List<Long> publicados = new ArrayList<>();
        Long lancarAPartirDoId = null;

        @Override
        public void publicar(NotificacaoPublicada msg) {
            if (lancarAPartirDoId != null && msg.id() >= lancarAPartirDoId) {
                throw new RuntimeException("broker indisponível (simulado)");
            }
            publicados.add(msg.id());
        }
    }
}
