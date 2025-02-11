package projetolivros.livros.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;
import projetolivros.livros.Model.GeneroLivro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AtualizarLivroDto{
    String isbn;
    String titulo;
    LocalDate dataPublicacao;
    BigDecimal preco;
    GeneroLivro genero;
    UUID idAutor;
}
