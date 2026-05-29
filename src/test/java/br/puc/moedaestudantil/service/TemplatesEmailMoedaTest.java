package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Professor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Os dois templates de e-mail do envio de moedas (Lab04S01). São funções puras
 * sobre Professor/Aluno, então o teste instancia o template direto — sem
 * contexto Micronaut nem broker.
 */
class TemplatesEmailMoedaTest {

    private final TemplatesEmailMoeda templates = new TemplatesEmailMoeda();

    private Professor professor() {
        return new Professor("Prof Ana", "ana@puc.br", null, "11111111111", "DCC", null, 400);
    }

    private Aluno aluno() {
        Aluno a = new Aluno("João Aluno", "joao@puc.br", null, "22222222222", "RG", "End", "Curso", null);
        a.setSaldo(100);
        return a;
    }

    @Test
    void recebimentoAlunoTrazAssuntoMotivoESaldoDoAluno() {
        MensagemEmail msg = templates.recebimentoAluno(professor(), aluno(), 100, "Excelente apresentação");

        assertEquals("Você recebeu 100 moedas", msg.assunto());
        assertTrue(msg.corpo().contains("João Aluno"), "deve saudar o aluno");
        assertTrue(msg.corpo().contains("Prof Ana"), "deve citar o professor remetente");
        assertTrue(msg.corpo().contains("Excelente apresentação"), "deve conter o motivo");
        assertTrue(msg.corpo().contains("100 moedas"), "deve informar o novo saldo do aluno");
    }

    @Test
    void confirmacaoProfessorTrazAssuntoAlunoMotivoESaldoRestante() {
        MensagemEmail msg = templates.confirmacaoProfessor(professor(), aluno(), 100, "Excelente apresentação");

        assertEquals("Confirmação: você enviou 100 moedas", msg.assunto());
        assertTrue(msg.corpo().contains("Prof Ana"), "deve saudar o professor");
        assertTrue(msg.corpo().contains("João Aluno"), "deve citar o aluno destinatário");
        assertTrue(msg.corpo().contains("Excelente apresentação"), "deve conter o motivo");
        assertTrue(msg.corpo().contains("400"), "deve informar o saldo restante do professor");
    }
}
