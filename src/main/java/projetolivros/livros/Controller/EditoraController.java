package projetolivros.livros.Controller;

import projetolivros.livros.Dto.EditoraDto;
import projetolivros.livros.Model.Editora;
import projetolivros.livros.Service.EditoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/editoras")
public class EditoraController {

    private final EditoraService editoraService;

    @Autowired
    public EditoraController(EditoraService editoraService) {
        this.editoraService = editoraService;
    }

    // Endpoint para listar todas as editoras
    @GetMapping
    public ResponseEntity<List<Editora>> listarTodas() {
        List<Editora> editoras = editoraService.listarTodas();
        return new ResponseEntity<>(editoras, HttpStatus.OK);
    }

    // Endpoint para buscar uma editora por ID
    @GetMapping("/{id}")
    public ResponseEntity<Editora> buscarPorId(@PathVariable UUID id) {
        Optional<Editora> editora = editoraService.buscarPorId(id);
        return editora.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint para salvar uma nova editora
    @PostMapping
    public ResponseEntity<Editora> salvar(@RequestBody EditoraDto editora) {
        editoraService.salvar(editora);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Endpoint para atualizar uma editora existente
    @PutMapping("/{id}")
    public ResponseEntity<Editora> atualizar(@PathVariable UUID id, @RequestBody Editora editora) {
        if (!editoraService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Editora updatedEditora = editoraService.atualizar(id, editora);
        return new ResponseEntity<>(updatedEditora, HttpStatus.OK);
    }

    // Endpoint para excluir uma editora
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        if (!editoraService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        editoraService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
