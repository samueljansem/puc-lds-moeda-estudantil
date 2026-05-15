package br.puc.moedaestudantil.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.CredencialDAO;
import br.puc.moedaestudantil.dao.EmpresaParceiraDAO;
import br.puc.moedaestudantil.dao.InstituicaoDAO;
import br.puc.moedaestudantil.dto.CadastroAlunoForm;
import br.puc.moedaestudantil.dto.CadastroEmpresaForm;
import br.puc.moedaestudantil.exception.CadastroDuplicadoException;
import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.EmpresaParceira;
import br.puc.moedaestudantil.model.Instituicao;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class ServicoCadastroTest {

    @Inject ServicoCadastro servico;
    @Inject AlunoDAO alunoDAO;
    @Inject EmpresaParceiraDAO empresaDAO;
    @Inject CredencialDAO credencialDAO;
    @Inject InstituicaoDAO instituicaoDAO;

    @Test
    void cadastrarAluno_persisteEHasheiaSenha() {
        Instituicao inst = instituicaoDAO.findAll().iterator().next();
        var form = new CadastroAlunoForm("Aluno A", "aluno-a@x.com",
                "10010010001", "MG1", "Rua A", "SI", inst.getId(),
                "aluno.a", "minhasenha");

        Aluno aluno = servico.cadastrarAluno(form);

        assertEquals("Aluno A", aluno.getNome());
        assertEquals("10010010001", aluno.getCpf());
        // Senha gravada como hash BCrypt, nunca em texto puro
        String hash = aluno.getCredencial().getSenhaHash();
        assertNotEquals("minhasenha", hash);
        assertTrue(hash.startsWith("$2"));
        assertTrue(BCrypt.verifyer().verify("minhasenha".toCharArray(), hash).verified);
    }

    @Test
    void cadastrarAluno_rejeitaCpfDuplicado() {
        Instituicao inst = instituicaoDAO.findAll().iterator().next();
        var form1 = new CadastroAlunoForm("Aluno B1", "b1@x.com",
                "20020020002", "MG", "End", "C", inst.getId(), "b1", "senha123");
        var form2 = new CadastroAlunoForm("Aluno B2", "b2@x.com",
                "20020020002", "MG", "End", "C", inst.getId(), "b2", "senha123");

        servico.cadastrarAluno(form1);
        var ex = assertThrows(CadastroDuplicadoException.class, () -> servico.cadastrarAluno(form2));
        assertEquals("cpf", ex.getCampo());
    }

    @Test
    void cadastrarEmpresa_rejeitaCnpjDuplicado() {
        var form1 = new CadastroEmpresaForm("Empresa A", "empA@x.com",
                "11122233000144", "empA", "senha123");
        var form2 = new CadastroEmpresaForm("Empresa B", "empB@x.com",
                "11122233000144", "empB", "senha123");

        servico.cadastrarEmpresa(form1);
        var ex = assertThrows(CadastroDuplicadoException.class, () -> servico.cadastrarEmpresa(form2));
        assertEquals("cnpj", ex.getCampo());
    }

    @Test
    void cadastrarAluno_rejeitaLoginDuplicado() {
        Instituicao inst = instituicaoDAO.findAll().iterator().next();
        var form1 = new CadastroAlunoForm("Aluno C1", "c1@x.com",
                "30030030003", "MG", "End", "C", inst.getId(), "loginC", "senha123");
        var form2 = new CadastroAlunoForm("Aluno C2", "c2@x.com",
                "30030030004", "MG", "End", "C", inst.getId(), "loginC", "senha123");

        servico.cadastrarAluno(form1);
        var ex = assertThrows(CadastroDuplicadoException.class, () -> servico.cadastrarAluno(form2));
        assertEquals("login", ex.getCampo());
    }
}
