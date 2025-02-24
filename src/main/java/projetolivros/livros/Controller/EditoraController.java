package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.EditoraDto;
import projetolivros.livros.Model.Editora;
import projetolivros.livros.Service.EditoraService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Editoras", description = "Endpoints para gerenciamento de editoras")
@RestController
@RequestMapping("/editoras")
@RequiredArgsConstructor
public class EditoraController {

    private final EditoraService editoraService;


    @Operation(summary = "Lista todas as editoras")
    @ApiResponse(responseCode = "200", description = "Lista de editoras retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Editora>> listarTodas() {
        List<Editora> editoras = editoraService.listarTodas();
        return new ResponseEntity<>(editoras, HttpStatus.OK);
    }

    @Operation(summary = "Busca uma editora por ID")
    @ApiResponse(responseCode = "200", description = "Editora encontrada com sucesso")
    @ApiResponse(responseCode = "404", description = "Editora não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<Editora> buscarPorId(@PathVariable UUID id) {
        Optional<Editora> editora = editoraService.buscarPorId(id);
        return editora.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Salva uma nova editora")
    @ApiResponse(responseCode = "201", description = "Editora criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro na requisição")
    @PostMapping
    public ResponseEntity<Editora> salvar(@RequestBody EditoraDto editora) {
        editoraService.salvar(editora);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Atualiza uma editora existente")
    @ApiResponse(responseCode = "200", description = "Editora atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Editora não encontrada")
    @PutMapping("/{id}")
    public ResponseEntity<Editora> atualizar(@PathVariable UUID id, @RequestBody Editora editora) {
        if (!editoraService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Editora updatedEditora = editoraService.atualizar(id, editora);
        return new ResponseEntity<>(updatedEditora, HttpStatus.OK);
    }

    @Operation(summary = "Exclui uma editora")
    @ApiResponse(responseCode = "204", description = "Editora excluída com sucesso")
    @ApiResponse(responseCode = "404", description = "Editora não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        if (!editoraService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        editoraService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
