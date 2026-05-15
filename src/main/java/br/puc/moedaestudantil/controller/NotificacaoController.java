package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.EmpresaParceiraDAO;
import br.puc.moedaestudantil.dao.NotificacaoDAO;
import br.puc.moedaestudantil.dao.ProfessorDAO;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.util.HashMap;
import java.util.Map;

@Controller("/notificacoes")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class NotificacaoController {

    private final AlunoDAO alunoDAO;
    private final EmpresaParceiraDAO empresaDAO;
    private final ProfessorDAO professorDAO;
    private final NotificacaoDAO notificacaoDAO;

    public NotificacaoController(AlunoDAO alunoDAO,
                                 EmpresaParceiraDAO empresaDAO,
                                 ProfessorDAO professorDAO,
                                 NotificacaoDAO notificacaoDAO) {
        this.alunoDAO = alunoDAO;
        this.empresaDAO = empresaDAO;
        this.professorDAO = professorDAO;
        this.notificacaoDAO = notificacaoDAO;
    }

    @Get
    @View("notificacoes")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> listar(Authentication authentication) {
        String email = emailDoUsuarioLogado(authentication);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Minhas Notificações");
        model.put("email", email);
        if (email != null) {
            model.put("notificacoes", notificacaoDAO.findByDestinatarioOrderByCriadaEmDesc(email));
        } else {
            model.put("notificacoes", java.util.List.of());
        }
        return model;
    }

    private String emailDoUsuarioLogado(Authentication auth) {
        String login = auth.getName();
        return alunoDAO.findByCredencialLogin(login).map(a -> a.getEmail())
                .or(() -> empresaDAO.findByCredencialLogin(login).map(e -> e.getEmail()))
                .or(() -> professorDAO.findByCredencialLogin(login).map(p -> p.getEmail()))
                .orElse(null);
    }
}
