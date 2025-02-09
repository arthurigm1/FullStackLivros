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
        //  Autor autorentidade = autor.mapear();
        service.save(autorentidade);
        return new ResponseEntity( HttpStatus.CREATED);


    }

    @GetMapping("{id}")
    public ResponseEntity<AutorDto> obterDetalhes(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);

        return service.findById(idAutor) // Busca o autor pelo ID, retornando um Optional<Autor>.
                .map(autor -> { // Se o autor estiver presente, executa a conversão para DTO.
                    AutorDto dto = mapper.toDto(autor); // Converte a entidade Autor para AutorDto.
                    return ResponseEntity.ok(dto); // Retorna a resposta HTTP 200 (OK) com o DTO.
                }).orElse(ResponseEntity.notFound().build()); // Se o autor não for encontrado, retorna HTTP 404 (Not Found).

            /* Versão alternativa com if tradicional:
            Optional<Autor> autor = service.findById(idAutor); // Busca o autor pelo ID.

            if (autor.isPresent()) { // Verifica se o autor foi encontrado.
                Autor entidade = autor.get(); // Obtém o objeto Autor do Optional.
               AutorDto dto = new AutorDto(entidade.getId(), // Converte a entidade Autor para um DTO, extraindo seus atributos.
                                            entidade.getNome(), // Obtém o nome do autor.
                                            entidade.getDataNascimento(), // Obtém a data de nascimento do autor.
                                            entidade.getNacionalidade()); // Obtém a nacionalidade do autor.
              }
            return ResponseEntity.notFound().build(); // Retorna HTTP 404 caso o autor não seja encontrado.
            */

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

               /* .stream() // Converte a lista original em um fluxo (Stream<Autor>)
                .map(autor -> new AutorDto( // Para cada autor na lista, cria um novo AutorDto
                        autor.getId(), // Copia o ID do autor
                        autor.getNome(), // Copia o nome do autor
                        autor.getDataNascimento(), // Copia a data de nascimento do autor
                        autor.getNacionalidade())) // Copia a nacionalidade do autor
                        .toList(); // Converte o fluxo de volta para uma lista de AutorDt

                */

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
