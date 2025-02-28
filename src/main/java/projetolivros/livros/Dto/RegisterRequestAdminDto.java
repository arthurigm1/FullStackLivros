package projetolivros.livros.Dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestAdminDto (@NotBlank String email,
                                      @NotBlank String senha){
}
