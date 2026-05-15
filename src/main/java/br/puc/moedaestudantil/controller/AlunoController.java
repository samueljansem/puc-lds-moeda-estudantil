package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.InstituicaoDAO;
import br.puc.moedaestudantil.dto.CadastroAlunoForm;
import br.puc.moedaestudantil.dto.EditaAlunoForm;
import br.puc.moedaestudantil.exception.CadastroDuplicadoException;
import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.service.LinhaExtrato;
import br.puc.moedaestudantil.service.ServicoCadastro;
import br.puc.moedaestudantil.service.ServicoMoeda;
import br.puc.moedaestudantil.service.ServicoResgate;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/alunos")
public class AlunoController {

    private final ServicoCadastro servicoCadastro;
    private final AlunoDAO alunoDAO;
    private final InstituicaoDAO instituicaoDAO;
    private final ServicoMoeda servicoMoeda;
    private final ServicoResgate servicoResgate;

    public AlunoController(ServicoCadastro servicoCadastro,
                           AlunoDAO alunoDAO,
                           InstituicaoDAO instituicaoDAO,
                           ServicoMoeda servicoMoeda,
                           ServicoResgate servicoResgate) {
        this.servicoCadastro = servicoCadastro;
        this.alunoDAO = alunoDAO;
        this.instituicaoDAO = instituicaoDAO;
        this.servicoMoeda = servicoMoeda;
        this.servicoResgate = servicoResgate;
    }

    @Get("/cadastro")
    @View("aluno-cadastro")
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> formCadastro() {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Cadastro de Aluno");
        model.put("instituicoes", instituicaoDAO.findAll());
        return model;
    }

    @Post(value = "/cadastro", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> cadastrar(@Valid @Body CadastroAlunoForm form) {
        try {
            servicoCadastro.cadastrarAluno(form);
            return HttpResponse.seeOther(URI.create("/login?cadastrado=1"));
        } catch (CadastroDuplicadoException e) {
            return HttpResponse.ok(new ModelAndView<>("aluno-cadastro",
                    modelComErroDuplicado(form, e.getCampo())));
        }
    }

    @Get("/perfil")
    @View("aluno-perfil")
    @Secured("ALUNO")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> perfil(Authentication authentication) {
        Aluno aluno = carregarAlunoLogado(authentication);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Meu Perfil");
        model.put("aluno", aluno);
        return model;
    }

    @Get("/editar")
    @View("aluno-editar")
    @Secured("ALUNO")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> formEditar(Authentication authentication) {
        Aluno aluno = carregarAlunoLogado(authentication);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Editar Perfil");
        model.put("aluno", aluno);
        return model;
    }

    @Post(value = "/editar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Secured("ALUNO")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> editar(Authentication authentication, @Valid @Body EditaAlunoForm form) {
        Aluno aluno = carregarAlunoLogado(authentication);
        try {
            servicoCadastro.atualizarAluno(aluno.getId(), form);
            return HttpResponse.seeOther(URI.create("/alunos/perfil"));
        } catch (CadastroDuplicadoException e) {
            return HttpResponse.ok(new ModelAndView<>("aluno-editar", Map.of(
                    "titulo", "Editar Perfil",
                    "aluno", aluno,
                    "erroDuplicado", e.getCampo()
            )));
        }
    }

    @Get("/extrato")
    @View("aluno-extrato")
    @Secured("ALUNO")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> extrato(Authentication authentication) {
        Aluno aluno = carregarAlunoLogado(authentication);
        List<LinhaExtrato> linhas = new java.util.ArrayList<>();
        linhas.addAll(servicoMoeda.extratoAluno(aluno.getId()));
        linhas.addAll(servicoResgate.linhasExtratoAluno(aluno.getId()));
        linhas.sort(java.util.Comparator.comparing(LinhaExtrato::data).reversed());

        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Meu Extrato");
        model.put("aluno", aluno);
        model.put("linhas", linhas);
        return model;
    }

    @Post(value = "/desativar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Secured("ALUNO")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> desativar(Authentication authentication) {
        Aluno aluno = carregarAlunoLogado(authentication);
        servicoCadastro.desativarUsuario(aluno.getId());
        return HttpResponse.seeOther(URI.create("/logout"));
    }

    @Get("/cupons")
    @View("aluno-cupons")
    @Secured("ALUNO")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> cupons(Authentication authentication,
                                      @io.micronaut.http.annotation.QueryValue(defaultValue = "") String novo) {
        Aluno aluno = carregarAlunoLogado(authentication);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Meus Cupons");
        model.put("aluno", aluno);
        model.put("cupons", servicoResgate.listarCuponsDoAluno(aluno.getId()));
        model.put("codigoNovo", novo.isEmpty() ? null : novo);
        return model;
    }

    private Aluno carregarAlunoLogado(Authentication authentication) {
        return alunoDAO.findByCredencialLogin(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Aluno autenticado não encontrado"));
    }

    private Map<String, Object> modelComErroDuplicado(CadastroAlunoForm form, String campo) {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Cadastro de Aluno");
        model.put("instituicoes", instituicaoDAO.findAll());
        model.put("form", form);
        model.put("erroDuplicado", campo);
        return model;
    }
}
