package br.puc.moedaestudantil.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.puc.moedaestudantil.dao.CredencialDAO;
import br.puc.moedaestudantil.dao.TokenRecuperacaoDAO;
import br.puc.moedaestudantil.dao.UsuarioDAO;
import br.puc.moedaestudantil.exception.TokenRecuperacaoInvalidoException;
import br.puc.moedaestudantil.model.Credencial;
import br.puc.moedaestudantil.model.TokenRecuperacao;
import br.puc.moedaestudantil.model.Usuario;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Singleton
public class ServicoRecuperacaoSenha {

    private static final int BCRYPT_COST = 12;
    private static final int TTL_HORAS = 1;
    private static final int TAMANHO_BYTES_TOKEN = 32;

    private final UsuarioDAO usuarioDAO;
    private final CredencialDAO credencialDAO;
    private final TokenRecuperacaoDAO tokenDAO;
    private final ServicoNotificacao servicoNotificacao;
    private final SecureRandom random = new SecureRandom();

    public ServicoRecuperacaoSenha(UsuarioDAO usuarioDAO,
                                   CredencialDAO credencialDAO,
                                   TokenRecuperacaoDAO tokenDAO,
                                   ServicoNotificacao servicoNotificacao) {
        this.usuarioDAO = usuarioDAO;
        this.credencialDAO = credencialDAO;
        this.tokenDAO = tokenDAO;
        this.servicoNotificacao = servicoNotificacao;
    }

    @Transactional
    public void solicitar(String email) {
        Optional<Usuario> opt = usuarioDAO.findByEmail(email);
        if (opt.isEmpty()) {
            // Silencioso para não revelar quais e-mails estão cadastrados.
            return;
        }
        Usuario usuario = opt.get();
        if (!usuario.isAtivo()) {
            return;
        }
        String token = gerarToken();
        LocalDateTime agora = LocalDateTime.now();
        TokenRecuperacao tr = new TokenRecuperacao(
                usuario.getCredencial(), token, agora.plusHours(TTL_HORAS), agora);
        tokenDAO.save(tr);

        servicoNotificacao.enviar(
                usuario.getEmail(),
                "Recuperação de senha",
                "Olá, " + usuario.getNome() + "!\n\n" +
                        "Recebemos uma solicitação para redefinir sua senha. Para\n" +
                        "continuar, abra o link abaixo (válido por " + TTL_HORAS + " hora):\n\n" +
                        "http://localhost:8080/redefinir-senha?token=" + token + "\n\n" +
                        "Se você não solicitou, ignore este e-mail."
        );
    }

    @Transactional
    public void redefinir(String token, String senhaNova) {
        if (senhaNova == null || senhaNova.length() < 6) {
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 6 caracteres.");
        }
        TokenRecuperacao tr = tokenDAO.findByToken(token)
                .orElseThrow(() -> new TokenRecuperacaoInvalidoException("Token inválido."));
        if (tr.isUsado()) {
            throw new TokenRecuperacaoInvalidoException("Este link já foi usado.");
        }
        if (LocalDateTime.now().isAfter(tr.getExpiraEm())) {
            throw new TokenRecuperacaoInvalidoException("Este link expirou.");
        }

        Credencial c = tr.getCredencial();
        c.setSenhaHash(BCrypt.withDefaults().hashToString(BCRYPT_COST, senhaNova.toCharArray()));
        credencialDAO.update(c);

        tr.setUsado(true);
        tokenDAO.update(tr);
    }

    private String gerarToken() {
        byte[] bytes = new byte[TAMANHO_BYTES_TOKEN];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
