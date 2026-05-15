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
@DiscriminatorValue("ALUNO")
@Table(name = "aluno")
@Serdeable
public class Aluno extends Usuario {

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "{aluno.cpf.formato}")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String rg;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String endereco;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String curso;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private Instituicao instituicao;

    @Min(value = 0, message = "{aluno.saldo.naoNegativo}")
    @Column(nullable = false)
    private int saldo = 0;

    public Aluno() {
    }

    public Aluno(String nome,
                 String email,
                 Credencial credencial,
                 String cpf,
                 String rg,
                 String endereco,
                 String curso,
                 Instituicao instituicao) {
        super(nome, email, credencial);
        this.cpf = cpf;
        this.rg = rg;
        this.endereco = endereco;
        this.curso = curso;
        this.instituicao = instituicao;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public Instituicao getInstituicao() { return instituicao; }
    public void setInstituicao(Instituicao instituicao) { this.instituicao = instituicao; }

    public int getSaldo() { return saldo; }
    public void setSaldo(int saldo) { this.saldo = saldo; }
}
