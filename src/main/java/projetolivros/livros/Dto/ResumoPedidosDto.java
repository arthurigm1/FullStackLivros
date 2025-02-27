package projetolivros.livros.Dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ResumoPedidosDto {
    private BigDecimal valorTotal;
    private BigDecimal valorPago;
    private BigDecimal valorAguardandoPagamento;
    private long countPago;
    private long countAguardandoPagamento;


}
