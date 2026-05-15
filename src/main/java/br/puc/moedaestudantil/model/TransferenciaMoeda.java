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
@Table(name = "transferencia_moeda")
@Serdeable
public class TransferenciaMoeda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Min(value = 1, message = "{transferencia.valor.positivo}")
    @Column(nullable = false)
    private int valor;

    @NotBlank(message = "{transferencia.motivo.obrigatorio}")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String motivo;

    @NotNull
    @Column(name = "realizada_em", nullable = false)
    private LocalDateTime realizadaEm;

    public TransferenciaMoeda() {
    }

    public TransferenciaMoeda(Professor professor, Aluno aluno, int valor, String motivo, LocalDateTime realizadaEm) {
        this.professor = professor;
        this.aluno = aluno;
        this.valor = valor;
        this.motivo = motivo;
        this.realizadaEm = realizadaEm;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getRealizadaEm() { return realizadaEm; }
    public void setRealizadaEm(LocalDateTime realizadaEm) { this.realizadaEm = realizadaEm; }
}
