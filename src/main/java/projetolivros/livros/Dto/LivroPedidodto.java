package projetolivros.livros.Dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LivroPedidodto {
    private String tituloLivro;
    private int quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    public LivroPedidodto(String tituloLivro, int quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {
        this.tituloLivro = tituloLivro;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }
}
