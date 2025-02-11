package projetolivros.livros.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import projetolivros.livros.Model.LivroCarrinho;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LivroCarrinhoDto {
    private Long productId;
    private String image;
    private String name;
    private String price;
    private Integer quantity;
    public String getSubTotal() {
        return String.valueOf(BigDecimal.valueOf(Double.parseDouble(price.replace(",", "."))).multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP)).replace(".", ",");
    }
}
