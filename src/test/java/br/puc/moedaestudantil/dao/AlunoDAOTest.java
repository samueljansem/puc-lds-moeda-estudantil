package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Credencial;
import br.puc.moedaestudantil.model.Instituicao;
import br.puc.moedaestudantil.model.TipoAtor;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class AlunoDAOTest {

    @Inject AlunoDAO alunoDAO;
    @Inject CredencialDAO credencialDAO;
    @Inject InstituicaoDAO instituicaoDAO;

    @Test
    void existsByCpf_falseWhenAbsent_trueWhenPresent() {
        Instituicao inst = instituicaoDAO.findAll().iterator().next();
        Credencial c = credencialDAO.save(new Credencial("login.cpf", "hash", TipoAtor.ALUNO));
        Aluno a = new Aluno("Nome", "cpf-test@a.com", c, "11122233344",
                "RG", "End", "Curso", inst);
        alunoDAO.save(a);

        assertTrue(alunoDAO.existsByCpf("11122233344"));
        assertFalse(alunoDAO.existsByCpf("00000000000"));
    }

    @Test
    void existsByEmail_detectsDuplicate() {
        Instituicao inst = instituicaoDAO.findAll().iterator().next();
        Credencial c = credencialDAO.save(new Credencial("login.email", "hash", TipoAtor.ALUNO));
        Aluno a = new Aluno("Nome", "email-test@a.com", c, "22233344455",
                "RG", "End", "Curso", inst);
        alunoDAO.save(a);

        assertTrue(alunoDAO.existsByEmail("email-test@a.com"));
        assertFalse(alunoDAO.existsByEmail("ausente@a.com"));
    }
}
