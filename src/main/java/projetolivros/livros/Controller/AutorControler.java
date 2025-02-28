package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Controller.Mapper.AutorMapper;
import projetolivros.livros.Dto.AutorAdminDto;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> salvar(@RequestBody @Valid AutorDto autor) {
        service.save(mapper.toEntity(autor));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Obtém detalhes de um autor pelo ID")
    @ApiResponse(responseCode = "200", description = "Autor encontrado")
    @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    @GetMapping("{id}")
    public ResponseEntity<AutorDto> obterDetalhes(@PathVariable("id") UUID id) {
        return service.findById(id)
                .map(autor -> ResponseEntity.ok(mapper.toDto(autor)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Exclui um autor pelo ID")
    @ApiResponse(responseCode = "204", description = "Autor excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable("id") UUID id) {
        return service.findById(id)
                .map(autor -> {
                    service.delete(autor);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtém a lista de autores filtrando por nome e nacionalidade")
    @ApiResponse(responseCode = "200", description = "Lista de autores retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<AutorDto>> obterListaAutores(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {

        return ResponseEntity.ok(service.pesquisaByExample(nome, nacionalidade)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AutorAdminDto>> listarTodasAdmin() {
        return ResponseEntity.ok(service.listarTodasAdmin());
    }

    @Operation(summary = "Atualiza os dados de um autor pelo ID")
    @ApiResponse(responseCode = "204", description = "Autor atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> atualizar(@PathVariable("id") UUID id, @RequestBody @Valid AutorDto dto) {
        Optional<Autor> autorOptional = service.findById(id);
        if (autorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Autor autor = autorOptional.get();
        if (dto.nome() != null) autor.setNome(dto.nome());
        if (dto.dataNascimento() != null) autor.setDataNascimento(dto.dataNascimento());
        if (dto.nacionalidade() != null) autor.setNacionalidade(dto.nacionalidade());
        if(dto.img() != null) autor.setImg(dto.img());
        if (dto.descricao() !=  null) autor.setDescricao(dto.descricao());
        service.atualizar(autor);
        return ResponseEntity.noContent().build();
    }
}
