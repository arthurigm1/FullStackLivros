package projetolivros.livros.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class LivroCarrinhoRequestdto {
    private UUID livroId;
    private String titulo;
    private BigDecimal preco;
}
