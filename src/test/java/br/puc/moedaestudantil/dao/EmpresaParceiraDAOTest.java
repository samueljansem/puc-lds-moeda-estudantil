package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Credencial;
import br.puc.moedaestudantil.model.EmpresaParceira;
import br.puc.moedaestudantil.model.TipoAtor;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class EmpresaParceiraDAOTest {

    @Inject EmpresaParceiraDAO empresaDAO;
    @Inject CredencialDAO credencialDAO;

    @Test
    void existsByCnpj_detectsDuplicate() {
        Credencial c = credencialDAO.save(new Credencial("login.empresa", "hash", TipoAtor.EMPRESA_PARCEIRA));
        EmpresaParceira e = new EmpresaParceira("Acme", "acme@x.com", c, "12345678000199");
        empresaDAO.save(e);

        assertTrue(empresaDAO.existsByCnpj("12345678000199"));
        assertFalse(empresaDAO.existsByCnpj("99999999999999"));
    }
}
