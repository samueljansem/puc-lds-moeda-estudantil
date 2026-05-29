package br.puc.moedaestudantil.messaging;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;

@RabbitClient(AmqpTopologia.EXCHANGE)
public interface PublisherNotificacao {

    @Binding("") // fanout ignora routing key
    void publicar(NotificacaoPublicada msg);
}
