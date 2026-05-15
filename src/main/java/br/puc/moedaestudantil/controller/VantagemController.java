package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.EmpresaParceiraDAO;
import br.puc.moedaestudantil.model.EmpresaParceira;
import br.puc.moedaestudantil.model.Vantagem;
import br.puc.moedaestudantil.service.ServicoVantagem;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Part;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller("/empresas/vantagens")
@Secured("EMPRESA_PARCEIRA")
public class VantagemController {

    private final EmpresaParceiraDAO empresaDAO;
    private final ServicoVantagem servicoVantagem;

    public VantagemController(EmpresaParceiraDAO empresaDAO, ServicoVantagem servicoVantagem) {
        this.empresaDAO = empresaDAO;
        this.servicoVantagem = servicoVantagem;
    }

    @Get
    @View("empresa-vantagens")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> listar(Authentication authentication,
                                      @io.micronaut.http.annotation.QueryValue(defaultValue = "") String sucesso) {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Minhas Vantagens");
        model.put("empresa", empresa);
        model.put("vantagens", servicoVantagem.listarDaEmpresa(empresa));
        model.put("sucesso", sucesso);
        return model;
    }

    @Get("/cadastrar")
    @View("empresa-vantagem-form")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> formCadastrar(Authentication authentication) {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Nova Vantagem");
        model.put("empresa", empresa);
        model.put("acao", "/empresas/vantagens/cadastrar");
        model.put("vantagem", null);
        return model;
    }

    @Post(value = "/cadastrar", consumes = MediaType.MULTIPART_FORM_DATA)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public MutableHttpResponse<?> cadastrar(Authentication authentication,
                                            @Part("descricao") String descricao,
                                            @Part("custo") String custo,
                                            @Part("foto") @Nullable CompletedFileUpload foto)
            throws IOException {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        try {
            int custoInt = parseCusto(custo);
            byte[] bytesFoto = (foto != null && foto.getSize() > 0) ? foto.getBytes() : null;
            String ct = (foto != null && foto.getSize() > 0) ? foto.getContentType().map(t -> t.toString()).orElse(null) : null;
            servicoVantagem.cadastrar(empresa, descricao, custoInt, bytesFoto, ct);
            return HttpResponse.seeOther(URI.create("/empresas/vantagens?sucesso=criada"));
        } catch (IllegalArgumentException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Nova Vantagem");
            model.put("empresa", empresa);
            model.put("acao", "/empresas/vantagens/cadastrar");
            model.put("erro", e.getMessage());
            model.put("formDescricao", descricao);
            model.put("formCusto", custo);
            return HttpResponse.ok(new ModelAndView<>("empresa-vantagem-form", model));
        }
    }

    @Get("/{id}/editar")
    @View("empresa-vantagem-form")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> formEditar(Authentication authentication, @PathVariable Long id) {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        Vantagem v = servicoVantagem.buscarOuFalhar(id);
        if (!v.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalStateException("Vantagem não pertence à empresa logada.");
        }
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Editar Vantagem");
        model.put("empresa", empresa);
        model.put("vantagem", v);
        model.put("acao", "/empresas/vantagens/" + id + "/editar");
        return model;
    }

    @Post(value = "/{id}/editar", consumes = MediaType.MULTIPART_FORM_DATA)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public MutableHttpResponse<?> editar(Authentication authentication,
                                         @PathVariable Long id,
                                         @Part("descricao") String descricao,
                                         @Part("custo") String custo,
                                         @Part("foto") @Nullable CompletedFileUpload foto)
            throws IOException {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        try {
            int custoInt = parseCusto(custo);
            byte[] bytesFoto = (foto != null && foto.getSize() > 0) ? foto.getBytes() : null;
            String ct = (foto != null && foto.getSize() > 0) ? foto.getContentType().map(t -> t.toString()).orElse(null) : null;
            servicoVantagem.atualizar(id, empresa, descricao, custoInt, bytesFoto, ct);
            return HttpResponse.seeOther(URI.create("/empresas/vantagens?sucesso=editada"));
        } catch (IllegalArgumentException e) {
            Vantagem v = servicoVantagem.buscarOuFalhar(id);
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Editar Vantagem");
            model.put("empresa", empresa);
            model.put("vantagem", v);
            model.put("acao", "/empresas/vantagens/" + id + "/editar");
            model.put("erro", e.getMessage());
            return HttpResponse.ok(new ModelAndView<>("empresa-vantagem-form", model));
        }
    }

    @Post(value = "/{id}/desativar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> desativar(Authentication authentication, @PathVariable Long id) {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        servicoVantagem.desativar(id, empresa);
        return HttpResponse.seeOther(URI.create("/empresas/vantagens?sucesso=desativada"));
    }

    @Post(value = "/{id}/ativar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> ativar(Authentication authentication, @PathVariable Long id) {
        EmpresaParceira empresa = carregarEmpresaLogada(authentication);
        servicoVantagem.ativar(id, empresa);
        return HttpResponse.seeOther(URI.create("/empresas/vantagens?sucesso=ativada"));
    }

    private int parseCusto(String custo) {
        try {
            return Integer.parseInt(custo.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Custo deve ser um número inteiro.");
        }
    }

    private EmpresaParceira carregarEmpresaLogada(Authentication authentication) {
        return empresaDAO.findByCredencialLogin(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Empresa autenticada não encontrada"));
    }
}
