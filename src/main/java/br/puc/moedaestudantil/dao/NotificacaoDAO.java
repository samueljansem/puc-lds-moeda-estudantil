package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Notificacao;
import br.puc.moedaestudantil.model.StatusNotificacao;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface NotificacaoDAO extends CrudRepository<Notificacao, Long> {

    List<Notificacao> findByDestinatarioOrderByCriadaEmDesc(String destinatario);

    @Query("SELECT n FROM Notificacao n ORDER BY n.criadaEm DESC")
    List<Notificacao> listarOrdemDescrescente();

    // Outbox: drainer pega as mais antigas em batch FIFO.
    List<Notificacao> findFirst50ByStatusOrderByCriadaEmAsc(StatusNotificacao status);

    // UPDATE condicional: garante idempotência do consumer.
    // Mensagens duplicadas resultam em 0 linhas afetadas (no-op + ack).
    @Query("UPDATE Notificacao n SET n.status = br.puc.moedaestudantil.model.StatusNotificacao.ENVIADA " +
           "WHERE n.id = :id AND n.status = br.puc.moedaestudantil.model.StatusNotificacao.PENDENTE")
    int marcarEnviada(Long id);
}
