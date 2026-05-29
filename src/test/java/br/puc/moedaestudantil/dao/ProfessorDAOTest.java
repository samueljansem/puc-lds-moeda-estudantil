package br.puc.moedaestudantil.dao;

import br.puc.moedaestudantil.model.Credencial;
import br.puc.moedaestudantil.model.Instituicao;
import br.puc.moedaestudantil.model.Professor;
import br.puc.moedaestudantil.model.TipoAtor;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class ProfessorDAOTest {

    @Inject ProfessorDAO professorDAO;
    @Inject CredencialDAO credencialDAO;
    @Inject InstituicaoDAO instituicaoDAO;

    @Test
    void demoProfessorTemSaldoInicialDeMil() {
        Professor demo = professorDAO.findByCredencialLogin("demo.professor").orElseThrow();
        assertEquals(1000, demo.getSaldo());
        assertEquals("Maria Orientadora", demo.getNome());
        assertEquals("PUC Minas", demo.getInstituicao().getNome());
    }

    @Test
    void existsByCpf_falseWhenAbsent_trueWhenPresent() {
        Instituicao inst = instituicaoDAO.findAll().iterator().next();
        Credencial c = credencialDAO.save(new Credencial("prof.cpftest", "hash", TipoAtor.PROFESSOR));
        Professor p = new Professor("Prof Teste", "prof-teste@a.com", c,
                "55566677788", "Depto Teste", inst, 1000);
        professorDAO.save(p);

        assertTrue(professorDAO.existsByCpf("55566677788"));
        assertFalse(professorDAO.existsByCpf("00000000000"));
    }
}
