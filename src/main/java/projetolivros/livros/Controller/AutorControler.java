package projetolivros.livros.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Controller.Mapper.AutorMapper;
import projetolivros.livros.Dto.AutorDto;
import projetolivros.livros.Model.Autor;
import projetolivros.livros.Service.AutorService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("autores")
@RequiredArgsConstructor
public class AutorControler {

    private final AutorService service;

    private final AutorMapper mapper;

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody @Valid AutorDto autor ) {
        Autor autorentidade = mapper.toEntity(autor);
        service.save(autorentidade);
        return new ResponseEntity( HttpStatus.CREATED);


    }

    @GetMapping("{id}")
    public ResponseEntity<AutorDto> obterDetalhes(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);

        return service.findById(idAutor)
                .map(autor -> {
                    AutorDto dto = mapper.toDto(autor);
                    return ResponseEntity.ok(dto);
                }).orElse(ResponseEntity.notFound().build());


    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);
        Optional<Autor> autor = service.findById(idAutor);
        if (autor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(autor.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AutorDto>> obterListaAutores(@RequestParam(value = "nome", required = false) String nome, @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {
        List<Autor> lista = service.pesquisaByExample(nome, nacionalidade);
        List<AutorDto> resultado = lista.stream().map(mapper::toDto).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> atualizar(@PathVariable("id") String id, @RequestBody AutorDto dto) {

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