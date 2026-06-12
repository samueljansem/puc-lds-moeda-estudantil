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
        // Após o login cada papel cai na sua lista de vantagens, não no perfil.
        // Aluno, professor e admin veem o catálogo (resgate só para o aluno);
        // a empresa cai na lista das próprias vantagens que oferece.
        return switch (role) {
            case "ALUNO", "PROFESSOR", "ADMIN" -> HttpResponse.seeOther(URI.create("/alunos/vantagens"));
            case "EMPRESA_PARCEIRA" -> HttpResponse.seeOther(URI.create("/empresas/vantagens"));
            default -> HttpResponse.seeOther(URI.create("/login"));
        };
    }

    // "Meu Perfil" no cabeçalho aponta para cá: leva cada papel ao seu perfil
    // (admin não tem perfil, vai para o início administrativo).
    @Get("/perfil")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public MutableHttpResponse<?> perfil(Authentication authentication) {
        String role = authentication.getRoles().stream().findFirst().orElse("");
        return switch (role) {
            case "ALUNO" -> HttpResponse.seeOther(URI.create("/alunos/perfil"));
            case "EMPRESA_PARCEIRA" -> HttpResponse.seeOther(URI.create("/empresas/perfil"));
            case "PROFESSOR" -> HttpResponse.seeOther(URI.create("/professores/perfil"));
            case "ADMIN" -> HttpResponse.seeOther(URI.create("/admin/inicio"));
            default -> HttpResponse.seeOther(URI.create("/login"));
        };
    }

    @Get("/sobre")
    @View("sobre")
    public Map<String, Object> sobre() {
        return Map.of("titulo", "Sobre");
    }
}
