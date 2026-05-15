package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.AlunoDAO;
import br.puc.moedaestudantil.dao.EmpresaParceiraDAO;
import br.puc.moedaestudantil.exception.SaldoInsuficienteException;
import br.puc.moedaestudantil.exception.VantagemIndisponivelException;
import br.puc.moedaestudantil.model.Aluno;
import br.puc.moedaestudantil.model.Resgate;
import br.puc.moedaestudantil.service.ServicoResgate;
import br.puc.moedaestudantil.service.ServicoVantagem;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.views.View;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller("/alunos/vantagens")
@Secured("ALUNO")
public class CatalogoController {

    private final AlunoDAO alunoDAO;
    private final EmpresaParceiraDAO empresaDAO;
    private final ServicoVantagem servicoVantagem;
    private final ServicoResgate servicoResgate;

    public CatalogoController(AlunoDAO alunoDAO,
                              EmpresaParceiraDAO empresaDAO,
                              ServicoVantagem servicoVantagem,
                              ServicoResgate servicoResgate) {
        this.alunoDAO = alunoDAO;
        this.empresaDAO = empresaDAO;
        this.servicoVantagem = servicoVantagem;
        this.servicoResgate = servicoResgate;
    }

    @Get
    @View("aluno-vantagens")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> catalogo(Authentication authentication,
                                        @QueryValue Optional<Integer> custoMaximo,
                                        @QueryValue Optional<Long> empresaId,
                                        @QueryValue(defaultValue = "") String erro) {
        Aluno aluno = carregarAlunoLogado(authentication);
        Integer custoMax = custoMaximo.orElse(null);
        Long empId = empresaId.orElse(null);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Catálogo de Vantagens");
        model.put("aluno", aluno);
        model.put("vantagens", servicoVantagem.filtrarCatalogo(custoMax, empId));
        model.put("empresas", empresaDAO.findAll());
        model.put("custoMaximo", custoMax);
        model.put("empresaIdSelecionada", empId);
        model.put("erro", erro);
        return model;
    }

    @Post(value = "/{id}/resgatar", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> resgatar(Authentication authentication, @PathVariable Long id) {
        Aluno aluno = carregarAlunoLogado(authentication);
        try {
            Resgate r = servicoResgate.resgatar(aluno.getId(), id);
            return HttpResponse.seeOther(URI.create("/alunos/cupons?novo=" + r.getCodigo()));
        } catch (SaldoInsuficienteException e) {
            return HttpResponse.seeOther(URI.create("/alunos/vantagens?erro=saldoInsuficiente"));
        } catch (VantagemIndisponivelException e) {
            return HttpResponse.seeOther(URI.create("/alunos/vantagens?erro=indisponivel"));
        }
    }

    private Aluno carregarAlunoLogado(Authentication authentication) {
        return alunoDAO.findByCredencialLogin(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Aluno autenticado não encontrado"));
    }
}
