package br.puc.moedaestudantil.model;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "resgate")
@Serdeable
public class Resgate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "vantagem_id", nullable = false)
    private Vantagem vantagem;

    @Min(value = 1)
    @Column(nullable = false)
    private int custo;

    @NotBlank
    @Size(max = 36)
    @Column(nullable = false, unique = true, length = 36)
    private String codigo;

    @NotNull
    @Column(name = "realizado_em", nullable = false)
    private LocalDateTime realizadoEm;

    public Resgate() {
    }

    public Resgate(Aluno aluno, Vantagem vantagem, int custo, String codigo, LocalDateTime realizadoEm) {
        this.aluno = aluno;
        this.vantagem = vantagem;
        this.custo = custo;
        this.codigo = codigo;
        this.realizadoEm = realizadoEm;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Vantagem getVantagem() { return vantagem; }
    public void setVantagem(Vantagem vantagem) { this.vantagem = vantagem; }

    public int getCusto() { return custo; }
    public void setCusto(int custo) { this.custo = custo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public LocalDateTime getRealizadoEm() { return realizadoEm; }
    public void setRealizadoEm(LocalDateTime realizadoEm) { this.realizadoEm = realizadoEm; }
}
