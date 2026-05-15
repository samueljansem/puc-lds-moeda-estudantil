package br.puc.moedaestudantil.model;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@DiscriminatorValue("PROFESSOR")
@Table(name = "professor")
@Serdeable
public class Professor extends Usuario {

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "{professor.cpf.formato}")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String departamento;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private Instituicao instituicao;

    @Min(value = 0, message = "{professor.saldo.naoNegativo}")
    @Column(nullable = false)
    private int saldo = 0;

    public Professor() {
    }

    public Professor(String nome,
                     String email,
                     Credencial credencial,
                     String cpf,
                     String departamento,
                     Instituicao instituicao,
                     int saldoInicial) {
        super(nome, email, credencial);
        this.cpf = cpf;
        this.departamento = departamento;
        this.instituicao = instituicao;
        this.saldo = saldoInicial;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public Instituicao getInstituicao() { return instituicao; }
    public void setInstituicao(Instituicao instituicao) { this.instituicao = instituicao; }

    public int getSaldo() { return saldo; }
    public void setSaldo(int saldo) { this.saldo = saldo; }
}
