package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.CredencialDAO;
import br.puc.moedaestudantil.dao.InstituicaoDAO;
import br.puc.moedaestudantil.dao.NotificacaoDAO;
import br.puc.moedaestudantil.dao.ProfessorDAO;
import br.puc.moedaestudantil.dao.TransferenciaMoedaDAO;
import br.puc.moedaestudantil.exception.SaldoInsuficienteException;
import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Credencial;
import br.puc.moedaestudantil.model.Instituicao;
import br.puc.moedaestudantil.model.Professor;
import br.puc.moedaestudantil.model.TipoAtor;
import br.puc.moedaestudantil.model.TransferenciaMoeda;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class ServicoMoedaTest {

    @Inject ServicoMoeda servicoMoeda;
    @Inject ProfessorDAO professorDAO;
    @Inject AlunoDAO alunoDAO;
    @Inject CredencialDAO credencialDAO;
    @Inject InstituicaoDAO instituicaoDAO;
    @Inject TransferenciaMoedaDAO transferenciaDAO;
    @Inject NotificacaoDAO notificacaoDAO;

    @Test
    void transferenciaSubtraiSaldoProfessorEAdicionaAoAlunoEGeraNotificacao() {
        Professor professor = criarProfessor("transf.prof1", "11144477733", 500, "PUC Minas");
        Aluno aluno = criarAluno("transf.aluno1", "44455566677", "transf.aluno1@a.com", professor.getInstituicao());

        TransferenciaMoeda t = servicoMoeda.transferir(
                professor.getId(), aluno.getId(), 100, "Excelente apresentação");

        assertEquals(400, professorDAO.findById(professor.getId()).orElseThrow().getSaldo());
        assertEquals(100, alunoDAO.findById(aluno.getId()).orElseThrow().getSaldo());
        assertEquals(100, t.getValor());

        var notifs = notificacaoDAO.findByDestinatarioOrderByCriadaEmDesc(aluno.getEmail());
        assertEquals(1, notifs.size());
        assertTrue(notifs.get(0).getCorpo().contains("Excelente apresentação"));
        assertTrue(notifs.get(0).getCorpo().contains("100 moedas"));
    }

    @Test
    void transferenciaGeraNotificacaoDeConfirmacaoParaProfessor() {
        Professor professor = criarProfessor("transf.prof5", "90000000015", 500, "PUC Minas");
        Aluno aluno = criarAluno("transf.aluno5", "90000000025", "transf.aluno5@a.com", professor.getInstituicao());

        servicoMoeda.transferir(professor.getId(), aluno.getId(), 100, "Bom trabalho");

        var notifsProf = notificacaoDAO.findByDestinatarioOrderByCriadaEmDesc(professor.getEmail());
        assertEquals(1, notifsProf.size());
        assertTrue(notifsProf.get(0).getCorpo().contains("Bom trabalho"));
        assertTrue(notifsProf.get(0).getCorpo().contains(aluno.getNome()));
        assertTrue(notifsProf.get(0).getCorpo().contains("400"), "deve informar o saldo restante do professor");
    }

    @Test
    void transferenciaRejeitadaParaAlunoDeOutraInstituicao() {
        Professor professor = criarProfessor("transf.prof6", "90000000035", 500, "PUC Minas");
        Instituicao outra = instituicaoDAO.findAll().stream()
                .filter(i -> i.getNome().equals("UFMG")).findFirst().orElseThrow();
        Aluno aluno = criarAluno("transf.aluno6", "90000000045", "transf.aluno6@a.com", outra);

        assertThrows(IllegalArgumentException.class,
                () -> servicoMoeda.transferir(professor.getId(), aluno.getId(), 100, "motivo"));

        assertEquals(500, professorDAO.findById(professor.getId()).orElseThrow().getSaldo());
        assertEquals(0, alunoDAO.findById(aluno.getId()).orElseThrow().getSaldo());
        assertTrue(transferenciaDAO.findByAlunoIdOrderByRealizadaEmDesc(aluno.getId()).isEmpty());
    }

    @Test
    void transferenciaRejeitadaParaAlunoInativo() {
        Professor professor = criarProfessor("transf.prof7", "90000000055", 500, "UFOP");
        Aluno aluno = criarAluno("transf.aluno7", "90000000065", "transf.aluno7@a.com", professor.getInstituicao());
        aluno.setAtivo(false);
        alunoDAO.update(aluno);

        assertThrows(IllegalArgumentException.class,
                () -> servicoMoeda.transferir(professor.getId(), aluno.getId(), 100, "motivo"));

        assertEquals(500, professorDAO.findById(professor.getId()).orElseThrow().getSaldo());
        assertEquals(0, alunoDAO.findById(aluno.getId()).orElseThrow().getSaldo());
    }

    @Test
    void transferenciaRejeitadaComSaldoInsuficiente() {
        Professor professor = criarProfessor("transf.prof2", "22255588844", 50, "UFMG");
        Aluno aluno = criarAluno("transf.aluno2", "55566677788", "transf.aluno2@a.com", professor.getInstituicao());

        assertThrows(SaldoInsuficienteException.class,
                () -> servicoMoeda.transferir(professor.getId(), aluno.getId(), 100, "qualquer motivo"));

        assertEquals(50, professorDAO.findById(professor.getId()).orElseThrow().getSaldo());
        assertEquals(0, alunoDAO.findById(aluno.getId()).orElseThrow().getSaldo());
        assertTrue(transferenciaDAO.findByAlunoIdOrderByRealizadaEmDesc(aluno.getId()).isEmpty());
    }

    @Test
    void transferenciaRejeitadaComMotivoVazio() {
        Professor professor = criarProfessor("transf.prof3", "33366699955", 500, "CEFET-MG");
        Aluno aluno = criarAluno("transf.aluno3", "66677788899", "transf.aluno3@a.com", professor.getInstituicao());

        assertThrows(IllegalArgumentException.class,
                () -> servicoMoeda.transferir(professor.getId(), aluno.getId(), 50, "   "));

        assertEquals(500, professorDAO.findById(professor.getId()).orElseThrow().getSaldo());
    }

    @Test
    void extratoAlunoMostraTransferenciasRecebidas() {
        Professor professor = criarProfessor("transf.prof4", "44477700066", 300, "UFOP");
        Aluno aluno = criarAluno("transf.aluno4", "77788899900", "transf.aluno4@a.com", professor.getInstituicao());

        servicoMoeda.transferir(professor.getId(), aluno.getId(), 10, "primeira");
        servicoMoeda.transferir(professor.getId(), aluno.getId(), 20, "segunda");

        List<LinhaExtrato> linhas = servicoMoeda.extratoAluno(aluno.getId());
        assertEquals(2, linhas.size());
        // ordem desc por data: segunda primeiro
        assertEquals("segunda", linhas.get(0).referencia());
        assertEquals(20, linhas.get(0).valor());
        assertEquals("primeira", linhas.get(1).referencia());
        assertEquals(10, linhas.get(1).valor());
    }

    private Professor criarProfessor(String login, String cpf, int saldo, String instituicaoNome) {
        Instituicao inst = instituicaoDAO.findAll().stream()
                .filter(i -> i.getNome().equals(instituicaoNome))
                .findFirst()
                .orElseThrow();
        Credencial c = credencialDAO.save(new Credencial(login, "hash", TipoAtor.PROFESSOR));
        Professor p = new Professor("Prof " + login, login + "@a.com", c,
                cpf, "Depto Teste", inst, saldo);
        return professorDAO.save(p);
    }

    private Aluno criarAluno(String login, String cpf, String email, Instituicao inst) {
        Credencial c = credencialDAO.save(new Credencial(login, "hash", TipoAtor.ALUNO));
        Aluno a = new Aluno("Aluno " + login, email, c, cpf, "RG", "End", "Curso", inst);
        return alunoDAO.save(a);
    }
}
