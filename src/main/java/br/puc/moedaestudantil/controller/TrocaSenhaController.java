package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.CredencialDAO;
import br.puc.moedaestudantil.dto.TrocaSenhaForm;
import br.puc.moedaestudantil.exception.SenhaIncorretaException;
import br.puc.moedaestudantil.model.Credencial;
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
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller("/trocar-senha")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class TrocaSenhaController {

    private final CredencialDAO credencialDAO;
    private final ServicoCadastro servicoCadastro;

    public TrocaSenhaController(CredencialDAO credencialDAO, ServicoCadastro servicoCadastro) {
        this.credencialDAO = credencialDAO;
        this.servicoCadastro = servicoCadastro;
    }

    @Get
    @View("trocar-senha")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> form(@QueryValue(defaultValue = "") String sucesso) {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Trocar Senha");
        model.put("sucesso", "1".equals(sucesso));
        return model;
    }

    @Post(consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> trocar(Authentication authentication, @Valid @Body TrocaSenhaForm form) {
        if (!form.senhaNova().equals(form.confirmacao())) {
            return paginaErro("As senhas não conferem.");
        }
        Credencial c = credencialDAO.findByLogin(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Credencial não encontrada."));
        try {
            servicoCadastro.trocarSenha(c.getId(), form.senhaAtual(), form.senhaNova());
            return HttpResponse.seeOther(URI.create("/trocar-senha?sucesso=1"));
        } catch (SenhaIncorretaException e) {
            return paginaErro("Senha atual incorreta.");
        } catch (IllegalArgumentException e) {
            return paginaErro(e.getMessage());
        }
    }

    private MutableHttpResponse<?> paginaErro(String erro) {
        return HttpResponse.ok(new ModelAndView<>("trocar-senha", Map.of(
                "titulo", "Trocar Senha",
                "erro", erro
        )));
    }
}
