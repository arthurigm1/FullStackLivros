package projetolivros.livros.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;


    public Usuario obterPorEmail(String email){
        return repository.findByEmail(email);
    }

}
