package br.puc.moedaestudantil.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.session.Session;

import java.net.URI;

@Controller("/logout")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class LogoutController {

    @Post
    public MutableHttpResponse<?> logout(Session session) {
        session.clear();
        return HttpResponse.seeOther(URI.create("/login"));
    }
}
