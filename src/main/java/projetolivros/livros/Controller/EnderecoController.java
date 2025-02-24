package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Model.Endereco;
import projetolivros.livros.Service.EnderecoService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Endereços", description = "Endpoints para gerenciamento de endereços")
@RestController
@RequestMapping("/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;


    @Operation(summary = "Busca um endereço pelo ID")
    @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<Endereco> buscarEndereco(@PathVariable UUID id) {
        Endereco endereco = enderecoService.buscarEnderecoPorId(id);
        return endereco != null ? ResponseEntity.ok(endereco) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Salva um novo endereço")
    @ApiResponse(responseCode = "201", description = "Endereço salvo com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro na requisição")
    @PostMapping
    public ResponseEntity<Endereco> salvarEndereco(@RequestBody Endereco endereco) {
        Endereco enderecoSalvo = enderecoService.salvarEndereco(endereco);
        return ResponseEntity.ok(enderecoSalvo);
    }

    @Operation(summary = "Lista os endereços do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de endereços retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Endereco>> buscarEnderecosPorUsuario() {
        List<Endereco> enderecos = enderecoService.buscarEnderecosPorUsuario();
        return ResponseEntity.ok(enderecos);
    }

    @Operation(summary = "Remove um endereço pelo ID")
    @ApiResponse(responseCode = "200", description = "Endereço removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerEndereco(@PathVariable UUID id) {
        enderecoService.removerEndereco(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Atualiza um endereço pelo ID")
    @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<Endereco> atualizarEndereco(@PathVariable UUID id, @RequestBody Endereco enderecoAtualizado) {
        Endereco enderecoSalvo = enderecoService.atualizarEndereco(id, enderecoAtualizado);
        return enderecoSalvo != null ? ResponseEntity.ok(enderecoSalvo) : ResponseEntity.notFound().build();
    }
}
