package projetolivros.livros.Dto;

import lombok.Getter;
import lombok.Setter;
import projetolivros.livros.Model.Enum.GeneroLivro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AtualizarLivroDto {
    private String isbn;
    private String titulo;
    private LocalDate dataPublicacao;
    private BigDecimal preco;
    private GeneroLivro genero;
    private UUID idAutor;
    private UUID idEditora;
    private String descricao;
    private String img;
    private int estoque;
}
