package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import projetolivros.livros.Model.Endereco;
import projetolivros.livros.Model.PasswordResetToken;
import projetolivros.livros.Model.Pedido;
import projetolivros.livros.Model.Usuario;

import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(UUID usuarioId);

    Usuario findByUsuarioId(Usuario usuario);

    Endereco findByEnderecoId(Endereco endereco);

}
