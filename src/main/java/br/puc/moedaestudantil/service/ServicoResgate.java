package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.ResgateDAO;
import br.puc.moedaestudantil.dao.VantagemDAO;
import br.puc.moedaestudantil.exception.SaldoInsuficienteException;
import br.puc.moedaestudantil.exception.VantagemIndisponivelException;
import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Resgate;
import br.puc.moedaestudantil.model.Vantagem;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Singleton
public class ServicoResgate {

    private final AlunoDAO alunoDAO;
    private final VantagemDAO vantagemDAO;
    private final ResgateDAO resgateDAO;
    private final ServicoNotificacao servicoNotificacao;

    public ServicoResgate(AlunoDAO alunoDAO,
                          VantagemDAO vantagemDAO,
                          ResgateDAO resgateDAO,
                          ServicoNotificacao servicoNotificacao) {
        this.alunoDAO = alunoDAO;
        this.vantagemDAO = vantagemDAO;
        this.resgateDAO = resgateDAO;
        this.servicoNotificacao = servicoNotificacao;
    }

    @Transactional
    public Resgate resgatar(Long alunoId, Long vantagemId) {
        Aluno aluno = alunoDAO.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado."));
        Vantagem vantagem = vantagemDAO.findById(vantagemId)
                .orElseThrow(() -> new VantagemIndisponivelException("Vantagem não encontrada."));

        if (!vantagem.isAtiva()) {
            throw new VantagemIndisponivelException("Vantagem indisponível para resgate.");
        }
        if (aluno.getSaldo() < vantagem.getCusto()) {
            throw new SaldoInsuficienteException(aluno.getSaldo(), vantagem.getCusto());
        }

        aluno.setSaldo(aluno.getSaldo() - vantagem.getCusto());
        alunoDAO.update(aluno);

        String codigo = UUID.randomUUID().toString();
        Resgate resgate = resgateDAO.save(new Resgate(
                aluno, vantagem, vantagem.getCusto(), codigo, LocalDateTime.now()
        ));

        // Cupom para o aluno
        servicoNotificacao.enviar(
                aluno.getEmail(),
                "Cupom de resgate — " + vantagem.getDescricao(),
                "Olá, " + aluno.getNome() + "!\n\n" +
                        "Seu resgate foi confirmado.\n\n" +
                        "Vantagem: " + vantagem.getDescricao() + "\n" +
                        "Custo: " + vantagem.getCusto() + " moedas\n" +
                        "Código de verificação: " + codigo + "\n\n" +
                        "Apresente este código ao retirar o benefício em\n" +
                        vantagem.getEmpresa().getNome() + ".\n\n" +
                        "Seu novo saldo é de " + aluno.getSaldo() + " moedas.",
                codigo
        );

        // Confirmação para a empresa
        servicoNotificacao.enviar(
                vantagem.getEmpresa().getEmail(),
                "Resgate confirmado — código " + codigo,
                "Olá,\n\n" +
                        "Um resgate da sua vantagem foi confirmado.\n\n" +
                        "Vantagem: " + vantagem.getDescricao() + "\n" +
                        "Aluno: " + aluno.getNome() + " (" + aluno.getEmail() + ")\n" +
                        "Código de verificação: " + codigo + "\n\n" +
                        "Quando o(a) aluno(a) apresentar este código, valide o resgate\n" +
                        "presencialmente.",
                codigo
        );

        return resgate;
    }

    public List<Resgate> listarCuponsDoAluno(Long alunoId) {
        return resgateDAO.findByAlunoIdOrderByRealizadoEmDesc(alunoId);
    }

    public List<LinhaExtrato> linhasExtratoAluno(Long alunoId) {
        return resgateDAO.findByAlunoIdOrderByRealizadoEmDesc(alunoId).stream()
                .map(r -> new LinhaExtrato(
                        r.getRealizadoEm(),
                        "Resgate",
                        "Cupom: " + r.getVantagem().getDescricao(),
                        -r.getCusto(),
                        r.getCodigo()
                ))
                .toList();
    }
}
