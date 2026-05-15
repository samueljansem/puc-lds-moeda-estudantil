package br.puc.moedaestudantil.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.puc.moedaestudantil.dao.CredencialDAO;
import br.puc.moedaestudantil.dao.UsuarioDAO;
import br.puc.moedaestudantil.model.Credencial;
import br.puc.moedaestudantil.model.TipoAtor;
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
    private final UsuarioDAO usuarioDAO;

    public ServicoAutenticacao(CredencialDAO credencialDAO, UsuarioDAO usuarioDAO) {
        this.credencialDAO = credencialDAO;
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    @NonNull
    public AuthenticationResponse authenticate(@Nullable HttpRequest<Object> requestContext,
                                               @NonNull AuthenticationRequest<String, String> authRequest) {
        String login = authRequest.getIdentity();
        String senha = authRequest.getSecret();

        return credencialDAO.findByLogin(login)
                .filter(c -> verificar(senha, c.getSenhaHash()))
                .map(this::responder)
                .orElseGet(() -> AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH));
    }

    private AuthenticationResponse responder(Credencial c) {
        // Admin não tem linha em `usuario`; sua conta nunca está "desativada".
        if (c.getTipoAtor() == TipoAtor.ADMIN) {
            return sucesso(c);
        }
        // Para demais papéis, a conta precisa estar ativa.
        boolean ativo = usuarioDAO.findByCredencialId(c.getId())
                .map(u -> u.isAtivo())
                .orElse(false);
        if (!ativo) {
            return AuthenticationResponse.failure(AuthenticationFailureReason.USER_DISABLED);
        }
        return sucesso(c);
    }

    private boolean verificar(String senhaPlana, String hash) {
        return BCrypt.verifyer().verify(senhaPlana.toCharArray(), hash).verified;
    }

    private AuthenticationResponse sucesso(Credencial c) {
        return AuthenticationResponse.success(c.getLogin(), List.of(c.getTipoAtor().name()));
    }
}
