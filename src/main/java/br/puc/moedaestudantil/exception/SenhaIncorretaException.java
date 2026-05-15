package br.puc.moedaestudantil.exception;

public class SenhaIncorretaException extends RuntimeException {
    public SenhaIncorretaException() {
        super("Senha atual incorreta.");
    }
}
