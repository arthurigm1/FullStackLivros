package projetolivros.livros.Validador;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import projetolivros.livros.Exceptions.RegistroException;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.UsuarioRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class UsuarioValidador {

    @Autowired
    private UsuarioRepository userRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public void validar(Usuario usuario) throws RegistroException {
        if(existeuser(usuario)) {
            throw new RegistroException("Usuario Ja cadastrado");
        }
    }

    private boolean existeuser(Usuario usuario) {
        Optional<Usuario> usuario1 = usuarioRepository.findById(usuario.getId());

        if (usuario1.isPresent()) {
            return true;
        }
        return false;
    }

}
