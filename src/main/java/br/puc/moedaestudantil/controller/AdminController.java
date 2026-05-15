package br.puc.moedaestudantil.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.views.View;

import java.util.Map;

@Controller("/admin")
public class AdminController {

    @Get("/inicio")
    @View("admin-inicio")
    @Secured("ADMIN")
    @Produces(MediaType.TEXT_HTML)
    public Map<String, Object> inicio() {
        return Map.of("titulo", "Administração");
    }
}
