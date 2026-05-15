package br.puc.moedaestudantil.controller;

import br.puc.moedaestudantil.dao.InstituicaoDAO;
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
class AlunoControllerTest {

    @Inject @Client("/") HttpClient client;
    @Inject InstituicaoDAO instituicaoDAO;

    @Test
    void getCadastro_retornaFormularioPublico() {
        HttpResponse<String> resp = client.toBlocking().exchange(
                HttpRequest.GET("/alunos/cadastro").accept(MediaType.TEXT_HTML),
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatus());
        assertTrue(resp.body().contains("Cadastro de Aluno"));
        assertTrue(resp.body().contains("PUC Minas"));   // veio do seed
    }

    @Test
    void postCadastro_redirecionaParaLoginEmSucesso() {
        Long instId = instituicaoDAO.findAll().iterator().next().getId();
        String body = "nome=João&email=joao@x.com&cpf=44455566677&rg=MG9&endereco=Rua+J"
                + "&curso=ADS&instituicaoId=" + instId
                + "&login=joao.x&senha=senha1234";

        HttpResponse<String> resp = client.toBlocking().exchange(
                HttpRequest.POST("/alunos/cadastro", body)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                String.class
        );

        assertEquals(HttpStatus.SEE_OTHER, resp.getStatus());
        assertNotNull(resp.getHeaders().get("Location"));
        assertTrue(resp.getHeaders().get("Location").contains("/login"));
    }

    @Test
    void postCadastro_renderizaFormComErroQuandoCpfDuplicado() {
        Long instId = instituicaoDAO.findAll().iterator().next().getId();
        String body1 = "nome=Maria&email=maria@x.com&cpf=55566677788&rg=MG&endereco=R"
                + "&curso=C&instituicaoId=" + instId + "&login=maria.x&senha=senha1234";
        String body2 = "nome=Outra&email=outra@x.com&cpf=55566677788&rg=MG&endereco=R"
                + "&curso=C&instituicaoId=" + instId + "&login=outra.x&senha=senha1234";

        client.toBlocking().exchange(
                HttpRequest.POST("/alunos/cadastro", body1)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                String.class
        );

        HttpResponse<String> resp = client.toBlocking().exchange(
                HttpRequest.POST("/alunos/cadastro", body2)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatus());
        assertTrue(resp.body().contains("Este CPF já está cadastrado"));
    }

    @Test
    void getPerfil_semLogin_naoEntregaConteudo() {
        // Com followRedirects=false: 303 retorna como response normal (não throw)
        // Sem o redirect, retornaria 401 e throw. Aceitamos ambos.
        HttpStatus status;
        try {
            HttpResponse<String> resp = client.toBlocking().exchange(
                    HttpRequest.GET("/alunos/perfil").accept(MediaType.TEXT_HTML),
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
