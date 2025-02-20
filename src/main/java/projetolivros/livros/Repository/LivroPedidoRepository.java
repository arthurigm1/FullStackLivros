package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.LivroPedido;
import projetolivros.livros.Model.PedidoPk;

import java.util.List;

public interface LivroPedidoRepository extends JpaRepository<LivroPedido, PedidoPk> {
    @Query("SELECT lp.livro FROM LivroPedido lp WHERE lp.pedido.id = :pedidoId")
    List<Livro> findLivrosByPedidoId(@Param("pedidoId") Long pedidoId);

}
