package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dto.RedefinirSenhaForm;
import br.puc.moedaestudantil.dto.SolicitarRecuperacaoForm;
import br.puc.moedaestudantil.exception.TokenRecuperacaoInvalidoException;
import br.puc.moedaestudantil.service.ServicoRecuperacaoSenha;
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
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class RecuperacaoSenhaController {

    private final ServicoRecuperacaoSenha servico;

    public RecuperacaoSenhaController(ServicoRecuperacaoSenha servico) {
        this.servico = servico;
    }

    @Get("/recuperar-senha")
    @View("recuperar-senha")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> formSolicitar(@QueryValue(defaultValue = "") String enviado) {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Recuperar Senha");
        model.put("enviado", "1".equals(enviado));
        return model;
    }

    @Post(value = "/recuperar-senha", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> solicitar(@Valid @Body SolicitarRecuperacaoForm form) {
        servico.solicitar(form.email());
        // Sempre redireciona com sucesso (não revelamos se o e-mail existe).
        return HttpResponse.seeOther(URI.create("/recuperar-senha?enviado=1"));
    }

    @Get("/redefinir-senha")
    @View("redefinir-senha")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> formRedefinir(@QueryValue String token) {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Redefinir Senha");
        model.put("token", token);
        return model;
    }

    @Post(value = "/redefinir-senha", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public MutableHttpResponse<?> redefinir(@Valid @Body RedefinirSenhaForm form) {
        if (!form.senhaNova().equals(form.confirmacao())) {
            return paginaErro(form.token(), "As senhas não conferem.");
        }
        try {
            servico.redefinir(form.token(), form.senhaNova());
            return HttpResponse.seeOther(URI.create("/login?cadastrado=1"));
        } catch (TokenRecuperacaoInvalidoException e) {
            return paginaErro(form.token(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return paginaErro(form.token(), e.getMessage());
        }
    }

    private MutableHttpResponse<?> paginaErro(String token, String erro) {
        return HttpResponse.ok(new ModelAndView<>("redefinir-senha", Map.of(
                "titulo", "Redefinir Senha",
                "token", token,
                "erro", erro
        )));
    }
}
