package projetolivros.livros.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import projetolivros.livros.Model.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Usuario findByNome(String nome);

    Optional<Usuario> findByEmail(String email);

}
