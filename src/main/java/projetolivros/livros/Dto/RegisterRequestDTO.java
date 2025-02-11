package projetolivros.livros.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(@NotNull @NotBlank String nome, @NotNull @NotBlank String email,@NotNull @NotBlank  String senha) {
}
