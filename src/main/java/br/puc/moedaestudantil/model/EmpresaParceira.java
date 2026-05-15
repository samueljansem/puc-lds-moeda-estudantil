package br.puc.moedaestudantil.model;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@DiscriminatorValue("EMPRESA_PARCEIRA")
@Table(name = "empresa_parceira")
@Serdeable
public class EmpresaParceira extends Usuario {

    @NotBlank
    @Pattern(regexp = "\\d{14}", message = "{empresa.cnpj.formato}")
    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    public EmpresaParceira() {
    }

    public EmpresaParceira(String nome, String email, Credencial credencial, String cnpj) {
        super(nome, email, credencial);
        this.cnpj = cnpj;
    }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}
