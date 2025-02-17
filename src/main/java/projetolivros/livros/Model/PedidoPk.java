package projetolivros.livros.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class PedidoPk implements Serializable {
    @Column(name = "pedido_id")
    private Long pedidoId;
    @Column(name = "livro_id")
    private UUID livroId;
}