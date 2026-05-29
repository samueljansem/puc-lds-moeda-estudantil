package br.puc.moedaestudantil.messaging;

import com.rabbitmq.client.Channel;
import io.micronaut.context.annotation.Context;
import io.micronaut.rabbitmq.connect.ChannelInitializer;
import jakarta.inject.Singleton;

import java.io.IOException;

/**
 * Topologia AMQP: fanout exchange {@code notificacoes} replica cada mensagem em
 * duas filas independentes — {@code .email} (entrega ao destinatário) e
 * {@code .webhook} (notificação ao sistema da empresa parceira).
 *
 * Fanout escolhido para demonstrar pub/sub real: uma única publicação dispara
 * dois side-effects independentes, falhas isoladas por canal.
 *
 * Declaração via {@link ChannelInitializer} (idempotente) garante que as
 * estruturas existem antes de qualquer listener consumir.
 */
@Context
@Singleton
public class AmqpTopologia extends ChannelInitializer {

    public static final String EXCHANGE = "notificacoes";
    public static final String QUEUE_EMAIL = "notificacoes.email";
    public static final String QUEUE_WEBHOOK = "notificacoes.webhook";

    @Override
    public void initialize(Channel channel, String name) throws IOException {
        channel.exchangeDeclare(EXCHANGE, "fanout", true);
        channel.queueDeclare(QUEUE_EMAIL, true, false, false, null);
        channel.queueDeclare(QUEUE_WEBHOOK, true, false, false, null);
        channel.queueBind(QUEUE_EMAIL, EXCHANGE, "");
        channel.queueBind(QUEUE_WEBHOOK, EXCHANGE, "");
    }
}
