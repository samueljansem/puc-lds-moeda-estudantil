package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.ProfessorDAO;
import br.puc.moedaestudantil.model.Professor;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.views.View;

import java.util.HashMap;
import java.util.Map;

@Controller("/professores")
public class ProfessorController {

    private final ProfessorDAO professorDAO;

    public ProfessorController(ProfessorDAO professorDAO) {
        this.professorDAO = professorDAO;
    }

    @Get("/perfil")
    @View("professor-perfil")
    @Secured("PROFESSOR")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> perfil(Authentication authentication) {
        Professor professor = carregarProfessorLogado(authentication);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Meu Perfil");
        model.put("professor", professor);
        return model;
    }

    private Professor carregarProfessorLogado(Authentication authentication) {
        return professorDAO.findByCredencialLogin(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Professor autenticado não encontrado"));
    }
}
