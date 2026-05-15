package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Credencial;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface CredencialDAO extends CrudRepository<Credencial, Long> {

    Optional<Credencial> findByLogin(String login);

    boolean existsByLogin(String login);
}
