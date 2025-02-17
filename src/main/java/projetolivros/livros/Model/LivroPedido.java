package projetolivros.livros.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@Entity
@Table(name = "livro_pedido")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LivroPedido {

    @EmbeddedId
    private PedidoPk id;  // Chave composta

    @ManyToOne
    @JoinColumn(name = "pedido_id", insertable = false, updatable = false)
    private Pedido pedido;  // Relacionamento com Pedido

    @ManyToOne
    @JoinColumn(name = "livro_id", insertable = false, updatable = false)
    private Livro livro;  // Relacionamento com Livro (sem @JoinColumn aqui!)

    private Integer quantidade;
    private BigDecimal preco;
}
