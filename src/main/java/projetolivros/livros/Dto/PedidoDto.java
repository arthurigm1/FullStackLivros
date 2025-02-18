package projetolivros.livros.Dto;

import lombok.*;
import projetolivros.livros.Model.Enum.StatusPedido;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor

public class PedidoDto {
    private Long id;
    private StatusPedido status;
    private BigDecimal valorTotal;
}
