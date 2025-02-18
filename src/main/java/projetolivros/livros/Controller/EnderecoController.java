package projetolivros.livros.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Model.Endereco;
import projetolivros.livros.Service.EnderecoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enderecos")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;



    @GetMapping("/{id}")
    public Endereco buscarEndereco(@PathVariable UUID id) {
        return enderecoService.buscarEnderecoPorId(id);
    }

    @PostMapping("")
    public ResponseEntity<Endereco> salvarEndereco(@RequestBody Endereco endereco) {
        Endereco enderecoSalvo = enderecoService.salvarEndereco(endereco);

        return ResponseEntity.ok(enderecoSalvo);
    }

    @GetMapping("")
    public ResponseEntity<List<Endereco>> buscarEnderecosPorUsuario() {

        List<Endereco> enderecos = enderecoService.buscarEnderecosPorUsuario();
        return ResponseEntity.ok(enderecos);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerEndereco(@PathVariable UUID id) {
        enderecoService.removerEndereco(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Endereco> atualizarEndereco(@PathVariable UUID id, @RequestBody Endereco enderecoAtualizado) {
        Endereco enderecoSalvo = enderecoService.atualizarEndereco(id, enderecoAtualizado);
        if (enderecoSalvo == null) {
            return ResponseEntity.notFound().build(); // Retorna 404 se o endereço não for encontrado
        }
        return ResponseEntity.ok(enderecoSalvo); // Retorna o endereço atualizado
    }

}