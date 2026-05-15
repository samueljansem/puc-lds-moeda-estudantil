package br.puc.moedaestudantil.service;

import br.puc.moedaestudantil.dao.NotificacaoDAO;
import br.puc.moedaestudantil.model.Notificacao;
import br.puc.moedaestudantil.model.StatusNotificacao;
import jakarta.inject.Singleton;

import java.time.LocalDateTime;

@Singleton
public class ServicoNotificacao {

    private final NotificacaoDAO notificacaoDAO;

    public ServicoNotificacao(NotificacaoDAO notificacaoDAO) {
        this.notificacaoDAO = notificacaoDAO;
    }

    public Notificacao enviar(String destinatario, String assunto, String corpo) {
        return enviar(destinatario, assunto, corpo, null);
    }

    public Notificacao enviar(String destinatario, String assunto, String corpo, String codigoReferencia) {
        Notificacao n = new Notificacao(
                destinatario,
                assunto,
                corpo,
                StatusNotificacao.ENVIADA,
                codigoReferencia,
                LocalDateTime.now()
        );
        return notificacaoDAO.save(n);
    }
}
