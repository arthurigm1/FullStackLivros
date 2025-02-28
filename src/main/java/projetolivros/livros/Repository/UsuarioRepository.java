package projetolivros.livros.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projetolivros.livros.Model.Usuario;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
   Usuario findByEmail(String email);
   Optional<Usuario> findById(UUID id);



   Usuario findByVerificationCode(String verificationCode);


}
