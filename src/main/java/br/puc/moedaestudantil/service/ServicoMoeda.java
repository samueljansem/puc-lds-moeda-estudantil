package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.ProfessorDAO;
import br.puc.moedaestudantil.dao.TransferenciaMoedaDAO;
import br.puc.moedaestudantil.exception.SaldoInsuficienteException;
import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Professor;
import br.puc.moedaestudantil.model.TransferenciaMoeda;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Singleton
public class ServicoMoeda {

    private final ProfessorDAO professorDAO;
    private final AlunoDAO alunoDAO;
    private final TransferenciaMoedaDAO transferenciaDAO;
    private final ServicoNotificacao servicoNotificacao;
    private final TemplatesEmailMoeda templates;

    public ServicoMoeda(ProfessorDAO professorDAO,
                        AlunoDAO alunoDAO,
                        TransferenciaMoedaDAO transferenciaDAO,
                        ServicoNotificacao servicoNotificacao,
                        TemplatesEmailMoeda templates) {
        this.professorDAO = professorDAO;
        this.alunoDAO = alunoDAO;
        this.transferenciaDAO = transferenciaDAO;
        this.servicoNotificacao = servicoNotificacao;
        this.templates = templates;
    }

    @Transactional
    public TransferenciaMoeda transferir(Long professorId, Long alunoId, int valor, String motivo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser positivo.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo é obrigatório.");
        }

        Professor professor = professorDAO.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado."));
        Aluno aluno = alunoDAO.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));

        if (!aluno.getInstituicao().getId().equals(professor.getInstituicao().getId())) {
            throw new IllegalArgumentException("O aluno não pertence à sua instituição.");
        }
        if (!aluno.isAtivo()) {
            throw new IllegalArgumentException("Não é possível enviar moedas para um aluno inativo.");
        }

        if (professor.getSaldo() < valor) {
            throw new SaldoInsuficienteException(professor.getSaldo(), valor);
        }

        professor.setSaldo(professor.getSaldo() - valor);
        aluno.setSaldo(aluno.getSaldo() + valor);
        professorDAO.update(professor);
        alunoDAO.update(aluno);

        String motivoLimpo = motivo.trim();
        TransferenciaMoeda transf = transferenciaDAO.save(new TransferenciaMoeda(
                professor, aluno, valor, motivoLimpo, LocalDateTime.now()
        ));

        MensagemEmail aoAluno = templates.recebimentoAluno(professor, aluno, valor, motivoLimpo);
        servicoNotificacao.enviar(aluno.getEmail(), aoAluno.assunto(), aoAluno.corpo());

        MensagemEmail aoProfessor = templates.confirmacaoProfessor(professor, aluno, valor, motivoLimpo);
        servicoNotificacao.enviar(professor.getEmail(), aoProfessor.assunto(), aoProfessor.corpo());

        return transf;
    }

    public List<LinhaExtrato> extratoProfessor(Long professorId) {
        return transferenciaDAO.findByProfessorIdOrderByRealizadaEmDesc(professorId).stream()
                .map(t -> new LinhaExtrato(
                        t.getRealizadaEm(),
                        "Envio",
                        "Para " + t.getAluno().getNome(),
                        -t.getValor(),
                        t.getMotivo()
                ))
                .toList();
    }

    public List<LinhaExtrato> extratoAluno(Long alunoId) {
        return transferenciaDAO.findByAlunoIdOrderByRealizadaEmDesc(alunoId).stream()
                .map(t -> new LinhaExtrato(
                        t.getRealizadaEm(),
                        "Recebido",
                        "De " + t.getProfessor().getNome(),
                        t.getValor(),
                        t.getMotivo()
                ))
                .sorted(Comparator.comparing(LinhaExtrato::data).reversed())
                .toList();
    }
}
