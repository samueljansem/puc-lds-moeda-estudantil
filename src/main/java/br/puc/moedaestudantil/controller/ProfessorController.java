package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.ProfessorDAO;
import br.puc.moedaestudantil.dto.TransferenciaForm;
import br.puc.moedaestudantil.exception.SaldoInsuficienteException;
import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Professor;
import br.puc.moedaestudantil.service.LinhaExtrato;
import br.puc.moedaestudantil.service.ServicoMoeda;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/professores")
@Secured("PROFESSOR")
public class ProfessorController {

    private final ProfessorDAO professorDAO;
    private final AlunoDAO alunoDAO;
    private final ServicoMoeda servicoMoeda;

    public ProfessorController(ProfessorDAO professorDAO,
                               AlunoDAO alunoDAO,
                               ServicoMoeda servicoMoeda) {
        this.professorDAO = professorDAO;
        this.alunoDAO = alunoDAO;
        this.servicoMoeda = servicoMoeda;
    }

    @Get("/perfil")
    @View("professor-perfil")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> perfil(Authentication authentication) {
        Professor professor = carregarProfessorLogado(authentication);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Meu Perfil");
        model.put("professor", professor);
        return model;
    }

    @Get("/transferir")
    @View("professor-transferir")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> formTransferir(Authentication authentication) {
        Professor professor = carregarProfessorLogado(authentication);
        return modelTransferir(professor, null, null, null);
    }

    @Post(value = "/transferir", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> transferir(Authentication authentication,
                                             @Valid @Body TransferenciaForm form) {
        Professor professor = carregarProfessorLogado(authentication);
        try {
            servicoMoeda.transferir(professor.getId(), form.alunoId(), form.valor(), form.motivo());
            return HttpResponse.seeOther(URI.create("/professores/extrato?sucesso=1"));
        } catch (SaldoInsuficienteException e) {
            return HttpResponse.ok(new ModelAndView<>("professor-transferir",
                    modelTransferir(professor, form, "saldoInsuficiente", null)));
        } catch (IllegalArgumentException e) {
            return HttpResponse.ok(new ModelAndView<>("professor-transferir",
                    modelTransferir(professor, form, "dadosInvalidos", e.getMessage())));
        }
    }

    @Get("/extrato")
    @View("professor-extrato")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> extrato(Authentication authentication,
                                       @QueryValue(defaultValue = "") String sucesso) {
        Professor professor = carregarProfessorLogado(authentication);
        List<LinhaExtrato> linhas = servicoMoeda.extratoProfessor(professor.getId());
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Meu Extrato");
        model.put("professor", professor);
        model.put("linhas", linhas);
        model.put("sucesso", "1".equals(sucesso));
        return model;
    }

    private Map<String, Object> modelTransferir(Professor professor,
                                                TransferenciaForm form,
                                                String erro,
                                                String detalheErro) {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Transferir Moedas");
        model.put("professor", professor);
        List<Aluno> alunos = alunoDAO.findByInstituicaoIdOrderByNome(professor.getInstituicao().getId());
        model.put("alunos", alunos);
        if (form != null) model.put("form", form);
        if (erro != null) model.put("erro", erro);
        if (detalheErro != null) model.put("detalheErro", detalheErro);
        return model;
    }

    private Professor carregarProfessorLogado(Authentication authentication) {
        return professorDAO.findByCredencialLogin(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Professor autenticado não encontrado"));
    }
}
