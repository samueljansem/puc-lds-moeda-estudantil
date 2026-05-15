package br.puc.moedaestudantil.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Serdeable
public record RedefinirSenhaForm(

        @NotBlank String token,

        @NotBlank @Size(min = 6, max = 100) String senhaNova,

        @NotBlank String confirmacao
) {
}
