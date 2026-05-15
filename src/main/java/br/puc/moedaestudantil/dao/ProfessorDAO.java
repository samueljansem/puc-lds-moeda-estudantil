package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Professor;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorDAO extends CrudRepository<Professor, Long> {

    Optional<Professor> findByCredencialLogin(String login);

    Optional<Professor> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    List<Professor> findAll();
}
