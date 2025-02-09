package projetolivros.livros.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.UsuarioRepository;
import projetolivros.livros.Validador.UsuarioValidador;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioValidador usuarioValidador;

    public void salvarUsuario(Usuario usuario) {
        var senha = usuario.getSenha();
        usuarioValidador.validar(usuario);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuarioRepository.save(usuario);
    }
    public Usuario buscarporLogin(String login) {
        return usuarioRepository.findByNome(login);
    }

    public Optional<Usuario> obterPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }



}
