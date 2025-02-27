package projetolivros.livros.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import projetolivros.livros.Model.Enum.StatusPedido;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PedidoTotalDto {
    private Long id;
    private BigDecimal valor;
    private StatusPedido status;
}
