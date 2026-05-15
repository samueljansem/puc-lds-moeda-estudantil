package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Instituicao;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface InstituicaoDAO extends CrudRepository<Instituicao, Long> {
}
