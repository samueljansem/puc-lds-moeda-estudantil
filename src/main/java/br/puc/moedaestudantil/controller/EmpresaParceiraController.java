package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.EmpresaParceiraDAO;
import br.puc.moedaestudantil.dto.CadastroEmpresaForm;
import br.puc.moedaestudantil.dto.EditaEmpresaForm;
import br.puc.moedaestudantil.exception.CadastroDuplicadoException;
import br.puc.moedaestudantil.model.EmpresaParceira;
import br.puc.moedaestudantil.service.ServicoCadastro;
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
import java.util.Map;

@Controller("/empresas")
public class EmpresaParceiraController {

    private final ServicoCadastro servicoCadastro;
    private final EmpresaParceiraDAO empresaDAO;

    public EmpresaParceiraController(ServicoCadastro servicoCadastro, EmpresaParceiraDAO empresaDAO) {
        this.servicoCadastro = servicoCadastro;
        this.empresaDAO = empresaDAO;
    }

    @Get("/cadastro")
    @View("empresa-cadastro")
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> formCadastro() {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Cadastro de Empresa Parceira");
        return model;
    }

    @Post(value = "/cadastro", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> cadastrar(@Valid @Body CadastroEmpresaForm form) {
        try {
            servicoCadastro.cadastrarEmpresa(form);
            return HttpResponse.seeOther(URI.create("/login?cadastrado=1"));
        } catch (CadastroDuplicadoException e) {
            return HttpResponse.ok(new ModelAndView<>("empresa-cadastro", Map.of(
                    "titulo", "Cadastro de Empresa Parceira",
                    "form", form,
                    "erroDuplicado", e.getCampo()
            )));
        }
    }

    @Get("/perfil")
    @View("empresa-perfil")
    @Secured("EMPRESA_PARCEIRA")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> perfil(Authentication authentication) {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Meu Perfil");
        model.put("empresa", empresa);
        return model;
    }

    @Get("/editar")
    @View("empresa-editar")
    @Secured("EMPRESA_PARCEIRA")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> formEditar(Authentication authentication) {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Editar Perfil");
        model.put("empresa", empresa);
        return model;
    }

    @Post(value = "/editar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Secured("EMPRESA_PARCEIRA")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> editar(Authentication authentication, @Valid @Body EditaEmpresaForm form) {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        try {
            servicoCadastro.atualizarEmpresa(empresa.getId(), form);
            return HttpResponse.seeOther(URI.create("/empresas/perfil"));
        } catch (CadastroDuplicadoException e) {
            return HttpResponse.ok(new ModelAndView<>("empresa-editar", Map.of(
                    "titulo", "Editar Perfil",
                    "empresa", empresa,
                    "erroDuplicado", e.getCampo()
            )));
        }
    }

    @Post(value = "/desativar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Secured("EMPRESA_PARCEIRA")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> desativar(Authentication authentication) {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        servicoCadastro.desativarUsuario(empresa.getId());
        return HttpResponse.seeOther(URI.create("/logout"));
    }

    private EmpresaParceira carregarEmpresaLogada(Authentication authentication) {
        return empresaDAO.findByCredencialLogin(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Empresa autenticada não encontrada"));
    }
}
