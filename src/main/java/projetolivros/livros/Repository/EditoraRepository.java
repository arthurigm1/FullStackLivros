package projetolivros.livros.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projetolivros.livros.Model.Editora;

import java.util.UUID;

public interface EditoraRepository extends JpaRepository<Editora, UUID> {
}
