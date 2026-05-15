package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Notificacao;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface NotificacaoDAO extends CrudRepository<Notificacao, Long> {

    List<Notificacao> findByDestinatarioOrderByCriadaEmDesc(String destinatario);

    @Query("SELECT n FROM Notificacao n ORDER BY n.criadaEm DESC")
    List<Notificacao> listarOrdemDescrescente();
}
