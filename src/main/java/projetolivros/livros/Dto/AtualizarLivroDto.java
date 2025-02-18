package projetolivros.livros.Dto;

import lombok.Getter;
import lombok.Setter;
import projetolivros.livros.Model.Enum.GeneroLivro;


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
