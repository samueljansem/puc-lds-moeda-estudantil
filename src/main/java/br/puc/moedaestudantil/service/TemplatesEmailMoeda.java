package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Professor;
import jakarta.inject.Singleton;

/**
 * Templates de e-mail (em pt-BR) do fluxo de transferência de moedas — um por
 * destinatário: o aluno (recebimento) e o professor (confirmação de envio).
 * Texto puro, coerente com a forma como a caixa de notificações o exibe.
 */
@Singleton
public class TemplatesEmailMoeda {

    /** E-mail ao aluno quando ele recebe uma transferência. */
    public MensagemEmail recebimentoAluno(Professor professor, Aluno aluno, int valor, String motivo) {
        return new MensagemEmail(
                "Você recebeu " + valor + " moedas",
                "Olá, " + aluno.getNome() + "!\n\n" +
                        "O(a) professor(a) " + professor.getNome() + " enviou " + valor +
                        " moedas para você.\n\n" +
                        "Motivo: " + motivo + "\n\n" +
                        "Seu novo saldo é de " + aluno.getSaldo() + " moedas."
        );
    }

    /** E-mail ao professor confirmando o envio de uma transferência. */
    public MensagemEmail confirmacaoProfessor(Professor professor, Aluno aluno, int valor, String motivo) {
        return new MensagemEmail(
                "Confirmação: você enviou " + valor + " moedas",
                "Olá, " + professor.getNome() + "!\n\n" +
                        "Você enviou " + valor + " moedas para " + aluno.getNome() + ".\n\n" +
                        "Motivo: " + motivo + "\n\n" +
                        "Seu saldo restante é de " + professor.getSaldo() + " moedas."
        );
    }
}
