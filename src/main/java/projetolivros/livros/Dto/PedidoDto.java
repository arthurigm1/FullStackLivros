package projetolivros.livros.Dto;

import lombok.*;
import projetolivros.livros.Model.Enum.StatusPedido;
import projetolivros.livros.Model.LivroPedido;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor

public class PedidoDto {
    private Long id;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private EnderecoDto endereco;
    private List<LivroPedidodto> itens;
}
