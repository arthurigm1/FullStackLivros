package projetolivros.livros.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Model.Endereco;
import projetolivros.livros.Service.EnderecoService;

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
}