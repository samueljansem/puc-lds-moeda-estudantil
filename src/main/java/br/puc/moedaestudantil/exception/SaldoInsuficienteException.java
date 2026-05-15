package br.puc.moedaestudantil.exception;

public class SaldoInsuficienteException extends RuntimeException {

    private final int saldoAtual;
    private final int valorSolicitado;

    public SaldoInsuficienteException(int saldoAtual, int valorSolicitado) {
        super("Saldo insuficiente: atual=" + saldoAtual + ", solicitado=" + valorSolicitado);
        this.saldoAtual = saldoAtual;
        this.valorSolicitado = valorSolicitado;
    }

    public int getSaldoAtual() { return saldoAtual; }
    public int getValorSolicitado() { return valorSolicitado; }
}
