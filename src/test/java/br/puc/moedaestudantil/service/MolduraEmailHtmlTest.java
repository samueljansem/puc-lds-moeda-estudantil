package br.puc.moedaestudantil.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MolduraEmailHtmlTest {

    @Test
    void corpoViraParagrafosEscapados() {
        String html = MolduraEmailHtml.render(
                "Assunto <teste>", "Olá, Fulano!\n\nLinha um\nLinha dois", null);

        assertTrue(html.contains("Assunto &lt;teste&gt;"));
        assertTrue(html.contains("<p style=\"margin:0 0 14px"));
        assertTrue(html.contains("Linha um<br>Linha dois"));
        assertFalse(html.contains("<teste>"));
    }

    @Test
    void urlViraLink() {
        String html = MolduraEmailHtml.render(
                "Recuperação", "Abra: http://localhost:8080/redefinir-senha?token=abc123", null);

        assertTrue(html.contains(
                "<a href=\"http://localhost:8080/redefinir-senha?token=abc123\""));
    }

    @Test
    void codigoViraCarimboQuandoPresente() {
        String comCodigo = MolduraEmailHtml.render("Cupom", "Corpo", "ABC-123");
        String semCodigo = MolduraEmailHtml.render("Cupom", "Corpo", null);

        assertTrue(comCodigo.contains(">ABC-123</div>"));
        assertFalse(semCodigo.contains("letter-spacing:4px"));
    }

}
