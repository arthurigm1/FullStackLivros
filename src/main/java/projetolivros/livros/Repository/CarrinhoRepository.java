package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projetolivros.livros.Model.Carrinho;
import projetolivros.livros.Model.LivroCarrinho;
import projetolivros.livros.Model.Usuario;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByUsuarioId(UUID usuario);
}
