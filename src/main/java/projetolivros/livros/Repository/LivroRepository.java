package projetolivros.livros.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import projetolivros.livros.Model.Autor;
import projetolivros.livros.Model.Livro;

import java.util.Optional;
import java.util.UUID;

public interface LivroRepository extends JpaRepository<Livro, UUID>, JpaSpecificationExecutor<Livro> {

    boolean existsByAutor(Autor autor); //retorna verdadeiro se existe livro!

    Optional<Livro> findByIsbn(String isbn);
}
