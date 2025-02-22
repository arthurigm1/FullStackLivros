package projetolivros.livros.Controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Controller.Mapper.UsuarioMapper;
import projetolivros.livros.Dto.LoginRequestDTO;
import projetolivros.livros.Dto.RegisterRequestDTO;
import projetolivros.livros.Dto.ResponseDTO;
import projetolivros.livros.Model.PasswordResetToken;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.PasswordResetTokenRepository;
import projetolivros.livros.Repository.UsuarioRepository;
import projetolivros.livros.Security.TokenService;
import projetolivros.livros.Service.PasswordResetService;
import projetolivros.livros.Service.UsuarioService;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;
    private final PasswordResetService passwordResetService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final UsuarioRepository usuarioRepository;



    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody @Valid LoginRequestDTO body) {
        Usuario user = this.repository.findByEmail(body.email());
        if (user == null) {
            return ResponseEntity.badRequest().body(new ResponseDTO("Erro", "Usuário não encontrado", null));
        }
        if (passwordEncoder.matches(body.senha(), user.getSenha())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getEmail(), token, user.getId()));
        }
        return ResponseEntity.badRequest().body(new ResponseDTO("Erro", "Senha incorreta", null));
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("code") String code) {
        return usuarioService.verify(code) ? "verify_success" : "verify_fail";
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody @Valid RegisterRequestDTO body)
            throws MessagingException, UnsupportedEncodingException {
        Usuario user = this.repository.findByEmail(body.email());
        if (user == null) {
            Usuario newUser = usuarioMapper.toEntity(body);
            newUser.setSenha(passwordEncoder.encode(body.senha()));
            usuarioService.registerUser(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(newUser.getNome(), token, newUser.getId()));
        }
        return ResponseEntity.badRequest().body(new ResponseDTO("Erro", "Email já cadastrado", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Usuário não encontrado.");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "E-mail de recuperação enviado!");
        // Remove qualquer token existente antes de criar um novo
        passwordResetTokenRepository.deleteByUsuarioId(usuario.getId());

        passwordResetService.enviarEmailRecuperacao(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Token inválido ou expirado");
                    return new RuntimeException("Token inválido ou expirado");
                });

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(newPassword));
        repository.save(usuario);
        passwordResetTokenRepository.delete(resetToken);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Senha redefinida com sucesso!");

        return ResponseEntity.ok(response);
    }


}
