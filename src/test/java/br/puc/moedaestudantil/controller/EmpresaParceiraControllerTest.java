package br.puc.moedaestudantil.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class EmpresaParceiraControllerTest {

    @Inject @Client("/") HttpClient client;

    @Test
    void getCadastro_retornaFormularioPublico() {
        HttpResponse<String> resp = client.toBlocking().exchange(
                HttpRequest.GET("/empresas/cadastro").accept(MediaType.TEXT_HTML),
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatus());
        assertTrue(resp.body().contains("Cadastro de Empresa Parceira"));
        assertTrue(resp.body().contains("CNPJ"));
    }

    @Test
    void postCadastro_redirecionaParaLoginEmSucesso() {
        String body = "nome=Acme+Ltda&email=acme@x.com&cnpj=12345678000199"
                + "&login=acme.x&senha=senha1234";

        HttpResponse<String> resp = client.toBlocking().exchange(
                HttpRequest.POST("/empresas/cadastro", body)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                String.class
        );

        assertEquals(HttpStatus.SEE_OTHER, resp.getStatus());
        assertNotNull(resp.getHeaders().get("Location"));
        assertTrue(resp.getHeaders().get("Location").contains("/login"));
    }

    @Test
    void postCadastro_renderizaFormComErroQuandoCnpjDuplicado() {
        String body1 = "nome=Beta&email=beta@x.com&cnpj=22222222000122"
                + "&login=beta.x&senha=senha1234";
        String body2 = "nome=Outra&email=outra-emp@x.com&cnpj=22222222000122"
                + "&login=outra-emp.x&senha=senha1234";

        client.toBlocking().exchange(
                HttpRequest.POST("/empresas/cadastro", body1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                String.class
        );

        HttpResponse<String> resp = client.toBlocking().exchange(
                HttpRequest.POST("/empresas/cadastro", body2)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatus());
        assertTrue(resp.body().contains("Este CNPJ já está cadastrado"));
    }

    @Test
    void postCadastro_renderizaFormComErroQuandoEmailDuplicado() {
        String body1 = "nome=Gama&email=gama@x.com&cnpj=33333333000133"
                + "&login=gama.x&senha=senha1234";
        String body2 = "nome=Delta&email=gama@x.com&cnpj=44444444000144"
                + "&login=delta.x&senha=senha1234";

        client.toBlocking().exchange(
                HttpRequest.POST("/empresas/cadastro", body1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                String.class
        );

        HttpResponse<String> resp = client.toBlocking().exchange(
                HttpRequest.POST("/empresas/cadastro", body2)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatus());
        assertTrue(resp.body().contains("Este e-mail já está em uso"));
    }

    @Test
    void getPerfil_semLogin_naoEntregaConteudo() {
        HttpStatus status;
        try {
            HttpResponse<String> resp = client.toBlocking().exchange(
                    HttpRequest.GET("/empresas/perfil").accept(MediaType.TEXT_HTML),
                    String.class
            );
            status = resp.getStatus();
        } catch (HttpClientResponseException ex) {
            status = ex.getResponse().getStatus();
        }
        assertTrue(status == HttpStatus.UNAUTHORIZED || status == HttpStatus.SEE_OTHER,
                "esperado 401 ou 303, veio " + status);
    }
}
