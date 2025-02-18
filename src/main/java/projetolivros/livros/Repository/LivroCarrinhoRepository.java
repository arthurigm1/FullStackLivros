package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projetolivros.livros.Model.CarrinhoPk;
import projetolivros.livros.Model.LivroCarrinho;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface LivroCarrinhoRepository extends JpaRepository<LivroCarrinho, Long> {
    List<LivroCarrinho> findByCarrinhoId(Long carrinhoId);
    Optional<LivroCarrinho> findById(CarrinhoPk carrinhoPk);

    Optional<LivroCarrinho> findByLivroId(UUID livroId);
    Optional<LivroCarrinho> findByCarrinhoIdAndLivroId(Long carrinhoId, UUID livroId);
}