package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import projetolivros.livros.Model.Favorito;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoritoRepository extends JpaRepository<Favorito, UUID> {
    boolean existsByUsuarioAndLivro(Usuario usuario, Livro livro);
    @Modifying
    @Query("SELECT f FROM Favorito f JOIN FETCH f.livro l LEFT JOIN FETCH l.autor WHERE f.usuario.id = :usuarioId")
    @Transactional
    List<Favorito> findByUsuarioId(@Param("usuarioId") UUID usuarioId);

    Favorito findByUsuarioIdAndLivroId(UUID usuarioId, UUID livroId);
}
