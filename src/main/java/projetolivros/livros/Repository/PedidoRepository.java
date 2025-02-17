package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projetolivros.livros.Model.PasswordResetToken;
import projetolivros.livros.Model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
