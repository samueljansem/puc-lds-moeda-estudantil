package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.dao.ProfessorDAO;
import br.puc.moedaestudantil.model.Professor;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class ServicoSemestre {

    private static final int MOEDAS_POR_SEMESTRE = 1000;

    private final ProfessorDAO professorDAO;

    public ServicoSemestre(ProfessorDAO professorDAO) {
        this.professorDAO = professorDAO;
    }

    /**
     * FR-004: concede 1.000 moedas a todos os professores, somando ao saldo
     * atual (o saldo acumula entre semestres em vez de ser zerado).
     * Retorna a quantidade de professores creditados.
     */
    @Transactional
    public int concederSemestre() {
        int contador = 0;
        for (Professor p : professorDAO.findAll()) {
            p.setSaldo(p.getSaldo() + MOEDAS_POR_SEMESTRE);
            professorDAO.update(p);
            contador++;
        }
        return contador;
    }
}
