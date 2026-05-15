package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.CredencialDAO;
import br.puc.moedaestudantil.dao.EmpresaParceiraDAO;
import br.puc.moedaestudantil.dao.InstituicaoDAO;
import br.puc.moedaestudantil.dao.NotificacaoDAO;
import br.puc.moedaestudantil.dao.ResgateDAO;
import br.puc.moedaestudantil.dao.VantagemDAO;
import br.puc.moedaestudantil.exception.SaldoInsuficienteException;
import br.puc.moedaestudantil.exception.VantagemIndisponivelException;
import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Credencial;
import br.puc.moedaestudantil.model.EmpresaParceira;
import br.puc.moedaestudantil.model.Instituicao;
import br.puc.moedaestudantil.model.Resgate;
import br.puc.moedaestudantil.model.TipoAtor;
import br.puc.moedaestudantil.model.Vantagem;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest(transactional = false)
class ServicoResgateTest {

    @Inject ServicoResgate servicoResgate;
    @Inject ServicoVantagem servicoVantagem;
    @Inject AlunoDAO alunoDAO;
    @Inject EmpresaParceiraDAO empresaDAO;
    @Inject VantagemDAO vantagemDAO;
    @Inject ResgateDAO resgateDAO;
    @Inject CredencialDAO credencialDAO;
    @Inject InstituicaoDAO instituicaoDAO;
    @Inject NotificacaoDAO notificacaoDAO;

    @Test
    @Transactional
    void resgatePadraoDebitaSaldoEEmitiCodigoUnicoEDuasNotificacoes() {
        Aluno aluno = criarAluno("resg.aluno1", "12312312300", "resg.aluno1@a.com", 200);
        EmpresaParceira empresa = criarEmpresa("resg.empresa1", "11000000000001", "resg.empresa1@a.com");
        Vantagem vantagem = servicoVantagem.cadastrar(empresa, "Cupom 10% restaurante", 100, null, null);

        Resgate r = servicoResgate.resgatar(aluno.getId(), vantagem.getId());

        assertEquals(100, alunoDAO.findById(aluno.getId()).orElseThrow().getSaldo());
        assertNotNull(r.getCodigo());
        assertEquals(36, r.getCodigo().length());

        var notifsAluno = notificacaoDAO.findByDestinatarioOrderByCriadaEmDesc(aluno.getEmail());
        var notifsEmpresa = notificacaoDAO.findByDestinatarioOrderByCriadaEmDesc(empresa.getEmail());
        assertEquals(1, notifsAluno.size());
        assertEquals(1, notifsEmpresa.size());
        assertEquals(r.getCodigo(), notifsAluno.get(0).getCodigoReferencia());
        assertEquals(r.getCodigo(), notifsEmpresa.get(0).getCodigoReferencia());
    }

    @Test
    @Transactional
    void resgateRejeitadoComSaldoInsuficiente() {
        Aluno aluno = criarAluno("resg.aluno2", "23423423400", "resg.aluno2@a.com", 50);
        EmpresaParceira empresa = criarEmpresa("resg.empresa2", "22000000000001", "resg.empresa2@a.com");
        Vantagem vantagem = servicoVantagem.cadastrar(empresa, "Vantagem cara", 100, null, null);

        assertThrows(SaldoInsuficienteException.class,
                () -> servicoResgate.resgatar(aluno.getId(), vantagem.getId()));

        assertEquals(50, alunoDAO.findById(aluno.getId()).orElseThrow().getSaldo());
        assertEquals(0, resgateDAO.findByAlunoIdOrderByRealizadoEmDesc(aluno.getId()).size());
    }

    @Test
    @Transactional
    void resgateRejeitadoSeVantagemEstaDesativada() {
        Aluno aluno = criarAluno("resg.aluno3", "34534534500", "resg.aluno3@a.com", 500);
        EmpresaParceira empresa = criarEmpresa("resg.empresa3", "33000000000001", "resg.empresa3@a.com");
        Vantagem vantagem = servicoVantagem.cadastrar(empresa, "Já desativada", 50, null, null);
        servicoVantagem.desativar(vantagem.getId(), empresa);

        assertThrows(VantagemIndisponivelException.class,
                () -> servicoResgate.resgatar(aluno.getId(), vantagem.getId()));

        assertEquals(500, alunoDAO.findById(aluno.getId()).orElseThrow().getSaldo());
    }

    @Test
    @Transactional
    void doisResgatesGeramCodigosDistintos() {
        Aluno aluno = criarAluno("resg.aluno4", "45645645600", "resg.aluno4@a.com", 1000);
        EmpresaParceira empresa = criarEmpresa("resg.empresa4", "44000000000001", "resg.empresa4@a.com");
        Vantagem v1 = servicoVantagem.cadastrar(empresa, "Cupom A", 100, null, null);
        Vantagem v2 = servicoVantagem.cadastrar(empresa, "Cupom B", 200, null, null);

        Resgate r1 = servicoResgate.resgatar(aluno.getId(), v1.getId());
        Resgate r2 = servicoResgate.resgatar(aluno.getId(), v2.getId());

        assertEquals(700, alunoDAO.findById(aluno.getId()).orElseThrow().getSaldo());
        assertEquals(2, resgateDAO.findByAlunoIdOrderByRealizadoEmDesc(aluno.getId()).size());
        // FR-015: cada resgate tem código único
        assertEquals(false, r1.getCodigo().equals(r2.getCodigo()));
    }

    @Test
    @Transactional
    void catalogoFiltradoPorCustoMaximoExcluiVantagensMaisCaras() {
        EmpresaParceira empresa = criarEmpresa("resg.empresa5", "55000000000001", "resg.empresa5@a.com");
        servicoVantagem.cadastrar(empresa, "Barata", 50, null, null);
        servicoVantagem.cadastrar(empresa, "Mediana", 200, null, null);
        servicoVantagem.cadastrar(empresa, "Cara", 1000, null, null);

        List<Vantagem> apenasAteCem = servicoVantagem.filtrarCatalogo(100, null);
        assertEquals(1, apenasAteCem.stream().filter(v -> v.getEmpresa().getId().equals(empresa.getId())).count());

        List<Vantagem> ateDuzentos = servicoVantagem.filtrarCatalogo(200, null);
        assertEquals(2, ateDuzentos.stream().filter(v -> v.getEmpresa().getId().equals(empresa.getId())).count());
    }

    private Aluno criarAluno(String login, String cpf, String email, int saldo) {
        Instituicao inst = instituicaoDAO.findAll().iterator().next();
        Credencial c = credencialDAO.save(new Credencial(login, "hash", TipoAtor.ALUNO));
        Aluno a = new Aluno("Aluno " + login, email, c, cpf, "RG", "End", "Curso", inst);
        a.setSaldo(saldo);
        return alunoDAO.save(a);
    }

    private EmpresaParceira criarEmpresa(String login, String cnpj, String email) {
        Credencial c = credencialDAO.save(new Credencial(login, "hash", TipoAtor.EMPRESA_PARCEIRA));
        EmpresaParceira e = new EmpresaParceira("Empresa " + login, email, c, cnpj);
        return empresaDAO.save(e);
    }
}
