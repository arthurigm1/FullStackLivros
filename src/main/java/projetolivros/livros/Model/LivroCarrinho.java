package projetolivros.livros.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

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
    @JsonIgnore
    @JsonBackReference
    private Carrinho carrinho;

    @ManyToOne
    @MapsId("livroId")
    @JoinColumn(name = "livro_id", insertable = false, updatable = false)
    private Livro livro;

    private Integer quantidade;

    private BigDecimal preco;
}