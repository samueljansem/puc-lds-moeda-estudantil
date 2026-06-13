package br.puc.moedaestudantil.service;

import java.util.regex.Pattern;

/**
 * Monta a versão HTML dos e-mails seguindo o sistema visual da Caderneta
 * (DESIGN.md): capa verde-louro com filete dourado, papel de registro,
 * código de verificação como carimbo. Cores em hex porque cliente de e-mail
 * não entende OKLCH — cada valor é a conversão do token de `app.css`.
 *
 * O corpo continua texto puro no banco e na parte text/plain; aqui ele só
 * ganha a moldura: parágrafos separados por linha em branco, URLs viram links.
 */
final class MolduraEmailHtml {

    // Tokens do DESIGN.md convertidos de OKLCH para sRGB hex.
    private static final String PAPER = "#f4f8f4";
    private static final String SURFACE = "#fcfefc";
    private static final String INK = "#17221c";
    private static final String INK_3 = "#68726b";
    private static final String LINE = "#d9e0da";
    private static final String LAUREL = "#266741";
    private static final String LAUREL_INK = "#195131";
    private static final String LAUREL_TINT = "#e4f5e8";
    private static final String LAUREL_TINT_LINE = "#c7e1cc";
    private static final String COVER = "#153f29";
    private static final String COVER_INK = "#f2f5ec";
    private static final String GOLD = "#d9b24f";
    private static final String GOLD_DEEP = "#aa7d25";

    private static final String FONTE = "'Archivo',Arial,sans-serif";
    private static final String FONTE_MONO = "ui-monospace,'SF Mono',Menlo,monospace";

    private static final Pattern URL = Pattern.compile("https?://[^\\s<]+");

    private MolduraEmailHtml() {
    }

    static String render(String assunto, String corpo, String codigoReferencia) {
        return """
                <!doctype html>
                <html lang="pt-BR">
                <body style="margin:0;padding:0;background-color:%1$s;">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:%1$s;">
                <tr><td align="center" style="padding:32px 16px;">
                <table role="presentation" width="560" cellpadding="0" cellspacing="0" style="width:560px;max-width:100%%;background-color:%2$s;border:1px solid %3$s;border-radius:10px;">
                  <tr><td style="background-color:%4$s;border-bottom:3px solid %5$s;border-radius:10px 10px 0 0;padding:18px 28px;">
                    <table role="presentation" cellpadding="0" cellspacing="0"><tr>
                      <td style="width:28px;height:28px;background-color:%6$s;border-radius:999px;color:%4$s;font:700 15px/28px Arial,sans-serif;text-align:center;">M</td>
                      <td style="padding-left:10px;color:%7$s;font:600 14px/1 %8$s;letter-spacing:2px;text-transform:uppercase;">Moeda Estudantil</td>
                    </tr></table>
                  </td></tr>
                  <tr><td style="padding:28px;">
                    <h1 style="margin:0 0 16px;color:%9$s;font:700 22px/1.3 %8$s;">%10$s</h1>
                %11$s%12$s  </td></tr>
                  <tr><td style="padding:14px 28px;border-top:1px solid %3$s;color:%13$s;font:400 12px/1.5 %8$s;">
                    Sistema de Moeda Estudantil — e-mail automático, não é necessário responder.
                  </td></tr>
                </table>
                </td></tr></table>
                </body></html>
                """.formatted(
                PAPER, SURFACE, LINE, COVER, GOLD_DEEP, GOLD, COVER_INK, FONTE,
                INK, escapar(assunto), paragrafos(corpo), carimbo(codigoReferencia), INK_3);
    }

    /** Parágrafos separados por linha em branco; quebra simples vira {@code <br>}. */
    private static String paragrafos(String corpo) {
        StringBuilder html = new StringBuilder();
        for (String paragrafo : corpo.split("\n\n")) {
            html.append("    <p style=\"margin:0 0 14px;color:").append(INK)
                    .append(";font:400 15px/1.55 ").append(FONTE).append(";\">")
                    .append(linkar(escapar(paragrafo)).replace("\n", "<br>"))
                    .append("</p>\n");
        }
        return html.toString();
    }

    /** O carimbo da página (.codigo-destaque): mono, espaçado, louro sobre tinta. */
    private static String carimbo(String codigo) {
        if (codigo == null) {
            return "";
        }
        return ("    <div style=\"margin:20px 0 6px;padding:14px 18px;background-color:" + LAUREL_TINT
                + ";border:1px solid " + LAUREL_TINT_LINE + ";border-radius:6px;color:" + LAUREL_INK
                + ";font:600 18px/1 " + FONTE_MONO + ";letter-spacing:4px;text-align:center;\">"
                + escapar(codigo) + "</div>\n");
    }

    /** Clientes de e-mail não auto-linkam HTML; URLs viram âncoras em louro. */
    private static String linkar(String texto) {
        return URL.matcher(texto).replaceAll(m ->
                "<a href=\"" + m.group() + "\" style=\"color:" + LAUREL + ";\">" + m.group() + "</a>");
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
