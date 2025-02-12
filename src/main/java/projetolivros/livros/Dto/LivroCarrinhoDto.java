package projetolivros.livros.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projetolivros.livros.Model.LivroCarrinho;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LivroCarrinhoDto {
    private UUID livroId;
    private String titulo;
    private BigDecimal preco;
    private Integer quantidade;

    public LivroCarrinhoDto(LivroCarrinho livroCarrinho) {
    }


}
