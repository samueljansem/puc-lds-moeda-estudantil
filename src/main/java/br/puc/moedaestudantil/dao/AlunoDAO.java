package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Aluno;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface AlunoDAO extends CrudRepository<Aluno, Long> {

    Optional<Aluno> findByCpf(String cpf);

    Optional<Aluno> findByEmail(String email);

    Optional<Aluno> findByCredencialLogin(String login);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
