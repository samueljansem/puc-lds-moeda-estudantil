package br.puc.moedaestudantil.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.puc.moedaestudantil.dao.CredencialDAO;
import br.puc.moedaestudantil.model.Credencial;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class ServicoAutenticacao implements HttpRequestAuthenticationProvider<Object> {

    private final CredencialDAO credencialDAO;

    public ServicoAutenticacao(CredencialDAO credencialDAO) {
        this.credencialDAO = credencialDAO;
    }

    @Override
    @NonNull
    public AuthenticationResponse authenticate(@Nullable HttpRequest<Object> requestContext,
                                               @NonNull AuthenticationRequest<String, String> authRequest) {
        String login = authRequest.getIdentity();
        String senha = authRequest.getSecret();

        return credencialDAO.findByLogin(login)
                .filter(c -> verificar(senha, c.getSenhaHash()))
                .<AuthenticationResponse>map(this::sucesso)
                .orElseGet(() -> AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
    }

    private boolean verificar(String senhaPlana, String hash) {
        return BCrypt.verifyer().verify(senhaPlana.toCharArray(), hash).verified;
    }

    private AuthenticationResponse sucesso(Credencial c) {
        return AuthenticationResponse.success(c.getLogin(), List.of(c.getTipoAtor().name()));
    }
}
