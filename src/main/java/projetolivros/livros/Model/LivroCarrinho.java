package projetolivros.livros.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "livro_carrinho")
@EqualsAndHashCode
public class LivroCarrinho {

    @EmbeddedId
    private CarrinhoPk id;

    @ManyToOne
    @MapsId("carrinhoId")
    @JoinColumn(name = "carrinho_id", insertable = false, updatable = false)
    private Carrinho carrinho;

    @ManyToOne
    @MapsId("livroId")
    @JoinColumn(name = "livro_id", insertable = false, updatable = false)
    private Livro livro;

    private Integer quantidade;

    private BigDecimal preco;
}