package br.puc.moedaestudantil.messaging;

import br.puc.moedaestudantil.model.Notificacao;
import br.puc.moedaestudantil.model.StatusNotificacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListenerEmailTest {

    @Test
    void entregaMarcaENVIADA() {
        var n = pendente(42L);
        var dao = new FakeNotificacaoDAO(List.of(n));

        new ListenerEmail(dao).onMessage(new NotificacaoPublicada(42L));

        assertEquals(StatusNotificacao.ENVIADA, dao.findById(42L).orElseThrow().getStatus());
    }

    @Test
    void mensagemDuplicadaENoOpIdempotente() {
        var n = pendente(43L);
        var dao = new FakeNotificacaoDAO(List.of(n));

        new ListenerEmail(dao).onMessage(new NotificacaoPublicada(43L));
        // Segunda entrega da mesma mensagem (duplicação aceitável no broker):
        // não deve lançar, e o estado permanece ENVIADA.
        new ListenerEmail(dao).onMessage(new NotificacaoPublicada(43L));

        assertEquals(StatusNotificacao.ENVIADA, dao.findById(43L).orElseThrow().getStatus());
    }

    @Test
    void idInexistenteLanca() {
        var dao = new FakeNotificacaoDAO(List.of());

        assertThrows(IllegalStateException.class,
                () -> new ListenerEmail(dao).onMessage(new NotificacaoPublicada(404L)));
    }

    private static Notificacao pendente(long id) {
        Notificacao n = new Notificacao(
                "aluno@x.com", "Cupom de resgate", "Corpo",
                StatusNotificacao.PENDENTE, "cod-" + id, LocalDateTime.now());
        n.setId(id);
        return n;
    }
}
