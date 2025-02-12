package projetolivros.livros.Controller;


import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projetolivros.livros.Controller.Mapper.UsuarioMapper;
import projetolivros.livros.Dto.LoginRequestDTO;
import projetolivros.livros.Dto.RegisterRequestDTO;
import projetolivros.livros.Dto.ResponseDTO;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.UsuarioRepository;
import projetolivros.livros.Security.TokenService;
import projetolivros.livros.Service.UsuarioService;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequestDTO body) {
        Usuario user = this.repository.findByEmail(body.email());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (passwordEncoder.matches(body.senha(), user.getSenha())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getEmail(), token, user.getId()));
        }
        return ResponseEntity.badRequest().build();
    }


    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody @Valid RegisterRequestDTO body) throws MessagingException, UnsupportedEncodingException {
        // Verifica se o usuário já existe
        Usuario user = this.repository.findByEmail(body.email());
        // Se o usuário não existir, cria um novo
        if (user == null) {
            Usuario newUser = usuarioMapper.toEntity(body);
            newUser.setSenha(passwordEncoder.encode(body.senha()));  // Codificando a senha
            usuarioService.registerUser(newUser);

            // Gera o token de autenticação
            String token = this.tokenService.generateToken(newUser);

            // Retorna a resposta com os dados do novo usuário
            return ResponseEntity.ok(new ResponseDTO(newUser.getNome(), token, newUser.getId()));
        }

        // Retorna resposta de erro, caso o usuário já exista
        return ResponseEntity.badRequest().body(new ResponseDTO("Erro", "Email já cadastrado", null));
    }
}

