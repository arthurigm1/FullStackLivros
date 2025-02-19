package projetolivros.livros.Dto;



import projetolivros.livros.Model.Enum.GeneroLivro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ResultadoLivroDto(
        UUID id,
        String isbn,
        String titulo,
        LocalDate dataPublicacao,
        BigDecimal preco,
        GeneroLivro genero,
        String descricao,
        String img,
        AutorDto autor



       ) {
}
