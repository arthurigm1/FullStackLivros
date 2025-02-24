package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Controller.Mapper.AutorMapper;
import projetolivros.livros.Dto.AutorDto;
import projetolivros.livros.Model.Autor;
import projetolivros.livros.Service.AutorService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Autores", description = "Endpoints para gerenciamento de autores")
@RestController
@RequestMapping("autores")
@RequiredArgsConstructor
public class AutorControler {

    private final AutorService service;
    private final AutorMapper mapper;

    @Operation(summary = "Cadastra um novo autor")
    @ApiResponse(responseCode = "201", description = "Autor cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro na validação dos dados")
    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid AutorDto autor) {
        Autor autorEntidade = mapper.toEntity(autor);
        service.save(autorEntidade);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Obtém detalhes de um autor pelo ID")
    @ApiResponse(responseCode = "200", description = "Autor encontrado")
    @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    @GetMapping("{id}")
    public ResponseEntity<AutorDto> obterDetalhes(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);

        return service.findById(idAutor)
                .map(autor -> ResponseEntity.ok(mapper.toDto(autor)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Exclui um autor pelo ID")
    @ApiResponse(responseCode = "204", description = "Autor excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);
        Optional<Autor> autor = service.findById(idAutor);
        if (autor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(autor.get());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtém a lista de autores filtrando por nome e nacionalidade")
    @ApiResponse(responseCode = "200", description = "Lista de autores retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<AutorDto>> obterListaAutores(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {

        List<Autor> lista = service.pesquisaByExample(nome, nacionalidade);
        List<AutorDto> resultado = lista.stream().map(mapper::toDto).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Atualiza os dados de um autor pelo ID")
    @ApiResponse(responseCode = "204", description = "Autor atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    @PutMapping("{id}")
    public ResponseEntity<Void> atualizar(@PathVariable("id") String id, @RequestBody @Valid AutorDto dto) {
        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = service.findById(idAutor);
        if (autorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var autorAtual = autorOptional.get();
        autorAtual.setNome(dto.nome());
        autorAtual.setDataNascimento(dto.dataNascimento());
        autorAtual.setNacionalidade(dto.nacionalidade());
        service.atualizar(autorAtual);
        return ResponseEntity.noContent().build();
    }
}
