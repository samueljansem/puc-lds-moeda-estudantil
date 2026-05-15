package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Resgate;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResgateDAO extends CrudRepository<Resgate, Long> {

    List<Resgate> findByAlunoIdOrderByRealizadoEmDesc(Long alunoId);

    Optional<Resgate> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
