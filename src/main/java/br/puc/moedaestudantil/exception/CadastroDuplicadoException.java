package br.puc.moedaestudantil.exception;

public class CadastroDuplicadoException extends RuntimeException {

    private final String campo;

    public CadastroDuplicadoException(String campo) {
        super("Cadastro já existe para o campo: " + campo);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
