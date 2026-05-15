package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.EmpresaParceira;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface EmpresaParceiraDAO extends CrudRepository<EmpresaParceira, Long> {

    Optional<EmpresaParceira> findByCnpj(String cnpj);

    Optional<EmpresaParceira> findByEmail(String email);

    Optional<EmpresaParceira> findByCredencialLogin(String login);

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);
}
