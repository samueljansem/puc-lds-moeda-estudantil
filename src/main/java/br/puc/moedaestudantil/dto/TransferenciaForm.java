package br.puc.moedaestudantil.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Serdeable
public record TransferenciaForm(

        @NotNull Long alunoId,

        @NotNull @Min(value = 1, message = "{transferencia.valor.positivo}") Integer valor,

        @NotBlank(message = "{transferencia.motivo.obrigatorio}") @Size(max = 500) String motivo
) {
}
