package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.NotificacaoDAO;
import br.puc.moedaestudantil.dao.ProfessorDAO;
import br.puc.moedaestudantil.service.ServicoSemestre;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.views.View;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller("/admin")
@Secured("ADMIN")
public class AdminController {

    private final ServicoSemestre servicoSemestre;
    private final ProfessorDAO professorDAO;
    private final NotificacaoDAO notificacaoDAO;

    public AdminController(ServicoSemestre servicoSemestre,
                           ProfessorDAO professorDAO,
                           NotificacaoDAO notificacaoDAO) {
        this.servicoSemestre = servicoSemestre;
        this.professorDAO = professorDAO;
        this.notificacaoDAO = notificacaoDAO;
    }

    @Get("/inicio")
    @View("admin-inicio")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> inicio() {
        return Map.of("titulo", "Administração");
    }

    @Get("/semestre")
    @View("admin-semestre")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> semestre(@QueryValue(defaultValue = "0") int creditados) {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Conceder Semestre");
        model.put("professores", professorDAO.findAll());
        model.put("creditados", creditados);
        return model;
    }

    @Post(value = "/semestre/conceder", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> conceder() {
        int n = servicoSemestre.concederSemestre();
        return HttpResponse.seeOther(URI.create("/admin/semestre?creditados=" + n));
    }

    @Get("/notificacoes")
    @View("admin-notificacoes")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> notificacoes() {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Caixa Global de Notificações");
        model.put("notificacoes", notificacaoDAO.listarOrdemDescrescente());
        return model;
    }
}
