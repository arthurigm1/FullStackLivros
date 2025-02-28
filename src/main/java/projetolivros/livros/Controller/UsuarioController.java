package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Controller.Mapper.UsuarioMapper;
import projetolivros.livros.Dto.AlterarSenhadto;
import projetolivros.livros.Dto.UsuarioAtualizardto;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Service.UsuarioService;

@RestController
@RequestMapping("usuario")
@RequiredArgsConstructor
@Tag(name = "Usuário", description = "Gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados do usuário autenticado.")
    @GetMapping("")
    public ResponseEntity<UsuarioAtualizardto> buscarUsuarioPorId() {
        Usuario usuario = usuarioService.obterDados();
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioMapper.toDto(usuario));
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados do usuário autenticado.")
    @PutMapping("")

    public ResponseEntity<UsuarioAtualizardto> atualizarUsuario(@RequestBody UsuarioAtualizardto usuarioDTO) {
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(usuarioDTO);
        return (usuarioAtualizado != null)
                ? ResponseEntity.ok(usuarioMapper.toDto(usuarioAtualizado))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Alterar senha", description = "Modifica a senha do usuário autenticado.")
    @PutMapping("/changepass")
    public ResponseEntity<String> alterarSenha(@RequestBody AlterarSenhadto alterarSenhaDTO) {
        boolean sucesso = usuarioService.alterarSenha(alterarSenhaDTO);
        return sucesso ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
