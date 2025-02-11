package projetolivros.livros.Dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import projetolivros.livros.Model.GeneroLivro;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CadastroLivroDto(@NotBlank(message = "campo obrigatorio!")

                               String isbn,
                               @NotBlank(message = "campo obrigatorio!")
                               String titulo,
                               @NotNull(message = "campo obrigatorio!")
                               @Past(message = "nao pode ser uma data futura")
                               LocalDate dataPublicacao,
                               BigDecimal preco,
                               @NotNull(message = "campo obrigatorio!")
                               GeneroLivro genero,
                               @NotNull(message = "campo obrigatorio")
                               UUID idAutor)
{
}
