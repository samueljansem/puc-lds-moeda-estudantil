package br.puc.moedaestudantil.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Serdeable
public record CadastroEmpresaForm(

        @NotBlank @Size(max = 120) String nome,

        @NotBlank @Email @Size(max = 120) String email,

        @NotBlank @Pattern(regexp = "\\d{14}", message = "{empresa.cnpj.formato}") String cnpj,

        @NotBlank @Size(max = 80) String login,

        @NotBlank @Size(min = 6, max = 100) String senha
) {
}
