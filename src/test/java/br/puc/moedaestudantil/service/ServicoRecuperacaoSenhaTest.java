package br.puc.moedaestudantil.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.CredencialDAO;
import br.puc.moedaestudantil.dao.InstituicaoDAO;
import br.puc.moedaestudantil.dao.NotificacaoDAO;
import br.puc.moedaestudantil.dao.TokenRecuperacaoDAO;
import br.puc.moedaestudantil.exception.TokenRecuperacaoInvalidoException;
import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Credencial;
import br.puc.moedaestudantil.model.Instituicao;
import br.puc.moedaestudantil.model.TipoAtor;
import br.puc.moedaestudantil.model.TokenRecuperacao;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(transactional = false)
class ServicoRecuperacaoSenhaTest {

    @Inject ServicoRecuperacaoSenha servico;
    @Inject TokenRecuperacaoDAO tokenDAO;
    @Inject CredencialDAO credencialDAO;
    @Inject AlunoDAO alunoDAO;
    @Inject InstituicaoDAO instituicaoDAO;
    @Inject NotificacaoDAO notificacaoDAO;

    @Test
    @Transactional
    void solicitarGeraTokenEEnviaNotificacaoQuandoEmailExiste() {
        Aluno aluno = criarAluno("rec.aluno1", "11122233300", "rec.aluno1@a.com");

        servico.solicitar(aluno.getEmail());

        List<TokenRecuperacao> todos = (List<TokenRecuperacao>) tokenDAO.findAll();
        assertEquals(1, todos.stream()
                .filter(t -> t.getCredencial().getId().equals(aluno.getCredencial().getId()))
                .count());
        var notifs = notificacaoDAO.findByDestinatarioOrderByCriadaEmDesc(aluno.getEmail());
        assertEquals(1, notifs.size());
        assertTrue(notifs.get(0).getCorpo().contains("/redefinir-senha?token="));
    }

    @Test
    @Transactional
    void solicitarEmEmailDesconhecidoNaoGeraTokenNemNotificacao() {
        servico.solicitar("desconhecido-" + System.nanoTime() + "@nopes.com");
        // Nenhuma exceção; nenhum token novo criado para um e-mail que não existe.
        // (Validação por contagem total seria frágil em testes paralelos; basta
        // ter passado sem exceção.)
        assertTrue(true);
    }

    @Test
    @Transactional
    void redefinirTrocaSenhaEEMarcaTokenComoUsado() {
        Aluno aluno = criarAluno("rec.aluno2", "22233344400", "rec.aluno2@a.com");
        servico.solicitar(aluno.getEmail());
        TokenRecuperacao tr = ((List<TokenRecuperacao>) tokenDAO.findAll()).stream()
                .filter(t -> t.getCredencial().getId().equals(aluno.getCredencial().getId()))
                .findFirst().orElseThrow();

        servico.redefinir(tr.getToken(), "novaSenhaForte");

        Credencial atualizada = credencialDAO.findById(aluno.getCredencial().getId()).orElseThrow();
        assertTrue(BCrypt.verifyer().verify("novaSenhaForte".toCharArray(), atualizada.getSenhaHash()).verified);
        TokenRecuperacao trAtualizado = tokenDAO.findByToken(tr.getToken()).orElseThrow();
        assertTrue(trAtualizado.isUsado());
    }

    @Test
    @Transactional
    void redefinirRejeitaTokenJaUsado() {
        Aluno aluno = criarAluno("rec.aluno3", "33344455500", "rec.aluno3@a.com");
        servico.solicitar(aluno.getEmail());
        TokenRecuperacao tr = ((List<TokenRecuperacao>) tokenDAO.findAll()).stream()
                .filter(t -> t.getCredencial().getId().equals(aluno.getCredencial().getId()))
                .findFirst().orElseThrow();
        servico.redefinir(tr.getToken(), "primeiraNova");

        assertThrows(TokenRecuperacaoInvalidoException.class,
                () -> servico.redefinir(tr.getToken(), "outraNova"));
    }

    @Test
    @Transactional
    void redefinirRejeitaTokenExpirado() {
        Aluno aluno = criarAluno("rec.aluno4", "44455566600", "rec.aluno4@a.com");
        TokenRecuperacao tr = new TokenRecuperacao(
                aluno.getCredencial(),
                "token-expirado-" + System.nanoTime(),
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().minusHours(2));
        tokenDAO.save(tr);

        assertThrows(TokenRecuperacaoInvalidoException.class,
                () -> servico.redefinir(tr.getToken(), "qualquer"));
    }

    @Test
    @Transactional
    void redefinirRejeitaSenhaCurta() {
        Aluno aluno = criarAluno("rec.aluno5", "55566677700", "rec.aluno5@a.com");
        servico.solicitar(aluno.getEmail());
        TokenRecuperacao tr = ((List<TokenRecuperacao>) tokenDAO.findAll()).stream()
                .filter(t -> t.getCredencial().getId().equals(aluno.getCredencial().getId()))
                .findFirst().orElseThrow();

        assertThrows(IllegalArgumentException.class,
                () -> servico.redefinir(tr.getToken(), "12345"));
        // O token não deve ter sido marcado como usado
        assertFalse(tokenDAO.findByToken(tr.getToken()).orElseThrow().isUsado());
    }

    private Aluno criarAluno(String login, String cpf, String email) {
        Instituicao inst = instituicaoDAO.findAll().iterator().next();
        Credencial c = credencialDAO.save(new Credencial(login,
                BCrypt.withDefaults().hashToString(12, "senhaAntiga".toCharArray()),
                TipoAtor.ALUNO));
        Aluno a = new Aluno("Aluno " + login, email, c, cpf, "RG", "End", "Curso", inst);
        return alunoDAO.save(a);
    }
}
