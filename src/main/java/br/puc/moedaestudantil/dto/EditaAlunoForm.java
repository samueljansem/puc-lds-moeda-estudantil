package br.puc.moedaestudantil.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public record EditaAlunoForm(

        @NotBlank @Email @Size(max = 120) String email,

        @NotBlank @Size(max = 255) String endereco,

        @NotBlank @Size(max = 120) String curso
) {
}
