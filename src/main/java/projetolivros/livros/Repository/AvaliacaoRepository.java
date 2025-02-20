package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projetolivros.livros.Model.AvaliacaoLivro;

import java.util.List;
import java.util.UUID;

public interface AvaliacaoRepository extends JpaRepository<AvaliacaoLivro, UUID> {
    List<AvaliacaoLivro> findByLivroId(UUID livroId);

}