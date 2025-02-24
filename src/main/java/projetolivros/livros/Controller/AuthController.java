package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.LoginRequestDTO;
import projetolivros.livros.Dto.RegisterRequestDTO;
import projetolivros.livros.Dto.ResponseDTO;
import projetolivros.livros.Exceptions.RegistroException;
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
import java.util.Optional;

@Tag(name = "Autenticação", description = "Endpoints para autenticação e gerenciamento de usuários")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final PasswordResetService passwordResetService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Operation(summary = "Realiza login do usuário")
    @ApiResponse(responseCode = "200", description = "Login bem-sucedido")
    @ApiResponse(responseCode = "400", description = "Usuário não encontrado ou senha incorreta")
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

    @Operation(summary = "Verifica a conta do usuário por meio de um código de verificação")
    @ApiResponse(responseCode = "200", description = "Conta verificada")
    @ApiResponse(responseCode = "400", description = "Código inválido")
    @GetMapping("/verify")
    public String verifyUser(@RequestParam("code") String code) {
        return usuarioService.verify(code) ? "verify_success" : "verify_fail";
    }

    @Operation(summary = "Registra um novo usuário")
    @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Usuário já existente")
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody @Valid RegisterRequestDTO body) {
        try {
            ResponseDTO response = usuarioService.registerUser(body);
            return ResponseEntity.ok(response);
        } catch (RegistroException e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(e.getMessage(), null, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseDTO("Erro interno no servidor", null, null));
        }
    }

    @Operation(summary = "Solicita recuperação de senha")
    @ApiResponse(responseCode = "200", description = "E-mail de recuperação enviado")
    @ApiResponse(responseCode = "400", description = "Usuário não encontrado")
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) throws MessagingException, UnsupportedEncodingException {
        String email = request.get("email");
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Usuário não encontrado.");
            return ResponseEntity.badRequest().body(response);
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "E-mail de recuperação enviado!");
        passwordResetTokenRepository.deleteByUsuarioId(usuario.getId());

        passwordResetService.enviarEmailRecuperacao(email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Redefine a senha do usuário")
    @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso")
    @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(newPassword));
        repository.save(usuario);
        passwordResetTokenRepository.delete(resetToken);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Senha redefinida com sucesso!");

        return ResponseEntity.ok(response);
    }
}
