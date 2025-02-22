package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projetolivros.livros.Model.AvaliacaoLivro;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvaliacaoRepository extends JpaRepository<AvaliacaoLivro, UUID> {
    List<AvaliacaoLivro> findByLivroId(UUID livroId);
    Optional<AvaliacaoLivro> findByUsuarioAndLivro(Usuario usuario, Livro livro);

}