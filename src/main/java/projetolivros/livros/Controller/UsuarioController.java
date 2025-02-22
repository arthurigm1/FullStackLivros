package projetolivros.livros.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Controller.Mapper.UsuarioMapper;
import projetolivros.livros.Dto.AlterarSenhadto;
import projetolivros.livros.Dto.UsuarioAtualizardto;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Service.UsuarioService;

@RestController
@RequestMapping("usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    private  final UsuarioMapper usuarioMapper;
    @GetMapping("")
    public ResponseEntity<UsuarioAtualizardto> buscarUsuarioPorId() {
        Usuario usuario = usuarioService.obterDados();
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        UsuarioAtualizardto usuarioDto = usuarioMapper.toDto(usuario);
        return ResponseEntity.ok(usuarioDto);
    }


    @PutMapping("")
    public ResponseEntity<UsuarioAtualizardto> atualizarUsuario(@RequestBody UsuarioAtualizardto usuarioDTO) {
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(usuarioDTO);

        if (usuarioAtualizado != null) {
            return ResponseEntity.ok(usuarioMapper.toDto(usuarioAtualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/changepass")
    public ResponseEntity<String> alterarSenha(@RequestBody AlterarSenhadto alterarSenhaDTO) {
        boolean sucesso = usuarioService.alterarSenha( alterarSenhaDTO);
        if (sucesso) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }




}
