package br.puc.moedaestudantil.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public record EditaEmpresaForm(

        @NotBlank @Size(max = 120) String nome,

        @NotBlank @Email @Size(max = 120) String email
) {
}
