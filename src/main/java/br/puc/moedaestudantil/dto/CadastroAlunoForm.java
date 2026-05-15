package br.puc.moedaestudantil.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Serdeable
public record CadastroAlunoForm(

        @NotBlank @Size(max = 120) String nome,

        @NotBlank @Email @Size(max = 120) String email,

        @NotBlank @Pattern(regexp = "\\d{11}", message = "{aluno.cpf.formato}") String cpf,

        @NotBlank @Size(max = 20) String rg,

        @NotBlank @Size(max = 255) String endereco,

        @NotBlank @Size(max = 120) String curso,

        @NotNull Long instituicaoId,

        @NotBlank @Size(max = 80) String login,

        @NotBlank @Size(min = 6, max = 100) String senha
) {
}
