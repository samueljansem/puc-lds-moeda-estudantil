package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Vantagem;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface VantagemDAO extends CrudRepository<Vantagem, Long> {

    List<Vantagem> findByEmpresaIdOrderByCriadaEmDesc(Long empresaId);

    @Query("SELECT v FROM Vantagem v WHERE v.ativa = TRUE ORDER BY v.custo ASC")
    List<Vantagem> listarAtivasOrdenadasPorCusto();

    @Query("SELECT v FROM Vantagem v WHERE v.ativa = TRUE" +
            " AND (:custoMaximo IS NULL OR v.custo <= :custoMaximo)" +
            " AND (:empresaId   IS NULL OR v.empresa.id = :empresaId)" +
            " ORDER BY v.custo ASC")
    List<Vantagem> filtrar(@Nullable Integer custoMaximo, @Nullable Long empresaId);
}
