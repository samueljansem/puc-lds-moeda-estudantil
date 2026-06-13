package br.puc.moedaestudantil.messaging;

import br.puc.moedaestudantil.model.Notificacao;
import br.puc.moedaestudantil.model.StatusNotificacao;
import br.puc.moedaestudantil.service.EnviadorEmail;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListenerEmailTest {

    @Test
    void entregaEnviaEmailEMarcaENVIADA() {
        var n = pendente(42L);
        var dao = new FakeNotificacaoDAO(List.of(n));
        var enviador = new EnviadorFake();

        new ListenerEmail(dao, enviador).onMessage(new NotificacaoPublicada(42L));

        assertEquals(StatusNotificacao.ENVIADA, dao.findById(42L).orElseThrow().getStatus());
        assertEquals(List.of("aluno@x.com"), enviador.enviados);
    }

    @Test
    void mensagemDuplicadaENoOpIdempotente() {
        var n = pendente(43L);
        var dao = new FakeNotificacaoDAO(List.of(n));
        var enviador = new EnviadorFake();

        new ListenerEmail(dao, enviador).onMessage(new NotificacaoPublicada(43L));
        // Segunda entrega da mesma mensagem (duplicação aceitável no broker):
        // não deve lançar nem reenviar o e-mail; o estado permanece ENVIADA.
        new ListenerEmail(dao, enviador).onMessage(new NotificacaoPublicada(43L));

        assertEquals(StatusNotificacao.ENVIADA, dao.findById(43L).orElseThrow().getStatus());
        assertEquals(1, enviador.enviados.size());
    }

    @Test
    void idInexistenteLanca() {
        var dao = new FakeNotificacaoDAO(List.of());

        assertThrows(IllegalStateException.class,
                () -> new ListenerEmail(dao, new EnviadorFake()).onMessage(new NotificacaoPublicada(404L)));
    }

    private static Notificacao pendente(long id) {
        Notificacao n = new Notificacao(
                "aluno@x.com", "Cupom de resgate", "Corpo",
                StatusNotificacao.PENDENTE, "cod-" + id, LocalDateTime.now());
        n.setId(id);
        return n;
    }

    private static class EnviadorFake implements EnviadorEmail {
        final List<String> enviados = new ArrayList<>();

        @Override
        public void enviar(String para, String assunto, String corpo) {
            enviados.add(para);
        }
    }
}
