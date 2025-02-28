    package projetolivros.livros.Controller;

    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.web.bind.annotation.*;
    import projetolivros.livros.Dto.*;
    import projetolivros.livros.Exceptions.RegistroException;
    import projetolivros.livros.Model.Enum.UsuarioRole;
    import projetolivros.livros.Model.Usuario;
    import projetolivros.livros.Repository.UsuarioRepository;
    import projetolivros.livros.Security.TokenService;
    import projetolivros.livros.Service.UsuarioService;

    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;
    import java.util.stream.Collectors;


    @RestController
    @RequestMapping("/admin")
    @RequiredArgsConstructor
    public class AuthAdminController {
        private final UsuarioRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final TokenService tokenService;
        private final UsuarioService service;

        @Operation(summary = "Login exclusivo para administradores")
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido")
        @ApiResponse(responseCode = "403", description = "Acesso negado")
        @PostMapping("/login")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Responseadmindto> adminLogin(@RequestBody @Valid LoginRequestDTO body) {
            Usuario user = this.repository.findByEmail(body.email());
            if (user == null) {
                return ResponseEntity.badRequest().body(new Responseadmindto("Erro", "Usuário não encontrado", null,null));
            }
            if (!passwordEncoder.matches(body.senha(), user.getSenha())) {
                return ResponseEntity.badRequest().body(new Responseadmindto("Erro", "Senha incorreta", null,null));
            }
            if (user.getRole() != UsuarioRole.ADMIN) {
                return ResponseEntity.status(403).body(
                        new Responseadmindto("Erro", "Acesso negado. Apenas administradores podem acessar.", null,null)
                ); }

            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new Responseadmindto(user.getEmail(), token, user.getId(),user.getRole().toString()));

        }

        @Operation(summary = "Lista todos os usuários com email e role")
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
        @GetMapping("/usuarios")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<UsuarioDto>> getAllUsers() {
            List<UsuarioDto> usuarios = repository.findAll().stream()
                    .map(user -> new UsuarioDto(user.getEmail(), user.getRole(),user.getId()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(usuarios);
        }

        @Operation(summary = "Excluir um usuário pelo ID")
        @ApiResponse(responseCode = "200", description = "Usuário excluído com sucesso")
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
            Optional<Usuario> usuarioOptional = repository.findById(id);

            if (usuarioOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Usuário não encontrado.");
            }
            repository.delete(usuarioOptional.get());
            return ResponseEntity.ok().build();
        }

        @Operation(summary = "Registra um novo usuário")
        @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso")
        @ApiResponse(responseCode = "400", description = "Usuário já existente")
        @PostMapping("/register")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ResponseDTO> register(@RequestBody @Valid RegisterRequestAdminDto body) {
            try {
                ResponseDTO response = service.registerUserADMIN(body);
                return ResponseEntity.ok(response);
            } catch (RegistroException e) {
                return ResponseEntity.badRequest().body(new ResponseDTO(e.getMessage(), null, null));
            } catch (Exception e) {
                return ResponseEntity.status(500).body(new ResponseDTO("Erro interno no servidor", null, null));
            }
        }

    }
