package projetolivros.livros.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import projetolivros.livros.Model.Enum.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class PedidoAdminDto {
    private Long id;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private EnderecoDto endereco;
    private List<LivroPedidodto> itens;
    private String email;
    private LocalDateTime dataCadastro;
}