package projetolivros.livros.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CarrinhoPk implements Serializable {



    @Column(name = "livro_id")
    private UUID livroId;

    @Column(name = "carrinho_id")
    private Long carrinhoId;


    public CarrinhoPk() {}

    public CarrinhoPk(UUID livroId, Long carrinhoId) {
        this.livroId = livroId;
        this.carrinhoId = carrinhoId;
    }

    public UUID getLivroId() {
        return livroId;
    }

    public void setLivroId(UUID livroId) {
        this.livroId = livroId;
    }

    public Long getCarrinhoId() {
        return carrinhoId;
    }

    public void setCarrinhoId(Long carrinhoId) {
        this.carrinhoId = carrinhoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrinhoPk that = (CarrinhoPk) o;
        return Objects.equals(livroId, that.livroId) && Objects.equals(carrinhoId, that.carrinhoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(livroId, carrinhoId);
    }
}
