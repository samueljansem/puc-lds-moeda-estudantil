package br.puc.moedaestudantil.model;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "vantagem")
@Serdeable
public class Vantagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaParceira empresa;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String descricao;

    @Min(value = 1, message = "{vantagem.custo.positivo}")
    @Column(nullable = false)
    private int custo;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "foto")
    private byte[] foto;

    @Column(name = "foto_content_type", length = 50)
    private String fotoContentType;

    @Column(nullable = false)
    private boolean ativa = true;

    @NotNull
    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    public Vantagem() {
    }

    public Vantagem(EmpresaParceira empresa, String descricao, int custo, LocalDateTime criadaEm) {
        this.empresa = empresa;
        this.descricao = descricao;
        this.custo = custo;
        this.criadaEm = criadaEm;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EmpresaParceira getEmpresa() { return empresa; }
    public void setEmpresa(EmpresaParceira empresa) { this.empresa = empresa; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getCusto() { return custo; }
    public void setCusto(int custo) { this.custo = custo; }

    public byte[] getFoto() { return foto; }
    public void setFoto(byte[] foto) { this.foto = foto; }

    public String getFotoContentType() { return fotoContentType; }
    public void setFotoContentType(String fotoContentType) { this.fotoContentType = fotoContentType; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public LocalDateTime getCriadaEm() { return criadaEm; }
    public void setCriadaEm(LocalDateTime criadaEm) { this.criadaEm = criadaEm; }

    public boolean temFoto() {
        return foto != null && foto.length > 0;
    }
}
