package br.puc.moedaestudantil.service;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Garante que o toggle por propriedade funciona: com {@code resend.api-key}
 * presente (via env var RESEND_API_KEY em produção) o contexto usa o Resend;
 * sem ela, cai no modo simulado em log.
 */
class EnviadorEmailWiringTest {

    @Test
    void comApiKeySelecionaResend() {
        try (var ctx = ApplicationContext.run(
                Map.of("resend.api-key", "re_chave_teste"), Environment.TEST)) {
            assertInstanceOf(EnviadorEmailResend.class, ctx.getBean(EnviadorEmail.class));
        }
    }

    @Test
    void semApiKeySelecionaLogSimulado() {
        try (var ctx = ApplicationContext.run(Environment.TEST)) {
            assertInstanceOf(EnviadorEmailLog.class, ctx.getBean(EnviadorEmail.class));
        }
    }
}
