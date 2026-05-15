package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Usuario;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface UsuarioDAO extends CrudRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.credencial.id = :credencialId")
    Optional<Usuario> findByCredencialId(Long credencialId);
}
