package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projetolivros.livros.Model.Favorito;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoritoRepository extends JpaRepository<Favorito, UUID> {
    boolean existsByUsuarioAndLivro(Usuario usuario, Livro livro);
    List<Favorito> findByUsuarioId(UUID usuarioId); // Mudança aqui
    // Busca um favorito baseado no ID do usuário e do livro
    Favorito findByUsuarioIdAndLivroId(UUID usuarioId, UUID livroId);
}
