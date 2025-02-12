package projetolivros.livros.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LivroFavoritoDto {
    private UUID id;
    private String titulo;
    private BigDecimal preco;
}
