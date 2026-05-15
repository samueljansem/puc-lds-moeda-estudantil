package br.puc.moedaestudantil.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.util.HashMap;
import java.util.Map;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class LoginController {

    @Get("/login")
    @View("login")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> login(@QueryValue(defaultValue = "") String erro,
                                     @QueryValue(defaultValue = "") String cadastrado) {
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Entrar");
        model.put("temErro", !erro.isEmpty());
        model.put("temCadastro", "1".equals(cadastrado));
        return model;
    }
}
