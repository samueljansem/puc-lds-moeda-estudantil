package br.puc.moedaestudantil.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class LoginControllerTest {

    @Inject @Client("/") HttpClient client;

    @Test
    void getLogin_retornaFormularioPublico() {
        HttpResponse<String> resp = client.toBlocking().exchange(
                HttpRequest.GET("/login").accept(MediaType.TEXT_HTML),
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatus());
        assertTrue(resp.body().contains("Entrar"));
        assertTrue(resp.body().contains("Login"));
        assertTrue(resp.body().contains("Senha"));
    }

    @Test
    void getLogin_comFlagCadastrado_exibeMensagemSucesso() {
        HttpResponse<String> resp = client.toBlocking().exchange(
                HttpRequest.GET("/login?cadastrado=1").accept(MediaType.TEXT_HTML),
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatus());
        assertTrue(resp.body().contains("Cadastro realizado"));
    }

    @Test
    void getLogin_comFlagErro_exibeMensagemErro() {
        HttpResponse<String> resp = client.toBlocking().exchange(
                HttpRequest.GET("/login?erro=1").accept(MediaType.TEXT_HTML),
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatus());
        assertTrue(resp.body().contains("Login ou senha inválidos"));
    }
}
