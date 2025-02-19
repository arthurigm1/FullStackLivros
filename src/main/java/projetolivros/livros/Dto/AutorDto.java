package projetolivros.livros.Dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record AutorDto(UUID id,
                       @NotBlank(message = "campo obrigatorio!")
                       @Size(min = 2, max = 100, message = "campo fora do tamanho maximo")
                       String nome,
                       @NotNull(message = "campo obrigatorio!")
                       @Past(message = "nao pode ser uma data futura")
                       LocalDate dataNascimento,
                       @NotBlank(message = "campo obrigatorio!")
                       @Size(min = 1,max = 50,message = "campo fora do tamanho maximo")
                       String nacionalidade,
                       @NotBlank(message = "campo obrigatorio!")
                       String descricao,
                       @NotBlank(message = "campo obrigatorio!")
                       String img) {


}
