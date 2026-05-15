package br.puc.moedaestudantil.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.net.URI;
import java.util.Map;

@Controller("/")
@Secured(SecurityRule.IS_ANONYMOUS)
public class HomeController {

    @Get
    public MutableHttpResponse<?> index(@Nullable Authentication authentication) {
        if (authentication == null) {
            return HttpResponse.seeOther(URI.create("/login"));
        }
        String role = authentication.getRoles().stream().findFirst().orElse("");
        return switch (role) {
            case "ALUNO" -> HttpResponse.seeOther(URI.create("/alunos/perfil"));
            case "EMPRESA_PARCEIRA" -> HttpResponse.seeOther(URI.create("/empresas/perfil"));
            default -> HttpResponse.seeOther(URI.create("/login"));
        };
    }

    @Get("/sobre")
    @View("sobre")
    public Map<String, Object> sobre() {
        return Map.of("titulo", "Sobre");
    }
}
