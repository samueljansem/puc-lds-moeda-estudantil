package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.TokenRecuperacao;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface TokenRecuperacaoDAO extends CrudRepository<TokenRecuperacao, Long> {

    Optional<TokenRecuperacao> findByToken(String token);
}
