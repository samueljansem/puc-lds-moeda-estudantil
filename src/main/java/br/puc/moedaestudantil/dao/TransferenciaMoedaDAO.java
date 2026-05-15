package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.TransferenciaMoeda;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface TransferenciaMoedaDAO extends CrudRepository<TransferenciaMoeda, Long> {

    List<TransferenciaMoeda> findByProfessorIdOrderByRealizadaEmDesc(Long professorId);

    List<TransferenciaMoeda> findByAlunoIdOrderByRealizadaEmDesc(Long alunoId);
}
