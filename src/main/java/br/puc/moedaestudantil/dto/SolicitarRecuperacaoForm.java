package br.puc.moedaestudantil.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record SolicitarRecuperacaoForm(
        @NotBlank @Email String email
) {
}
