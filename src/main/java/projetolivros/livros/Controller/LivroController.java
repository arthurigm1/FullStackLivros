package projetolivros.livros.Controller;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Controller.Mapper.LivroMapper;
import projetolivros.livros.Dto.AtualizarLivroDto;
import projetolivros.livros.Dto.CadastroLivroDto;
import projetolivros.livros.Dto.ResultadoLivroDto;
import projetolivros.livros.Model.Autor;

import projetolivros.livros.Model.Enum.GeneroLivro;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Repository.AutorRepository;
import projetolivros.livros.Security.SecurityService;
import projetolivros.livros.Service.LivroService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService service;
    private final LivroMapper mapper;
    private final AutorRepository autorRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Object> cadastrarLivro(@RequestBody @Valid CadastroLivroDto cadastroLivroDto) {
        Livro livro = mapper.toEntity(cadastroLivroDto);
        service.salvar(livro);
        return ResponseEntity.ok().build();

    }
    @GetMapping("{id}")
    public ResponseEntity<ResultadoLivroDto> obterDetalhes(@PathVariable("id") String id) {
        try {
            UUID livroId = UUID.fromString(id);
            return service.buscarPorId(livroId)
                    .map(livro -> {
                        var dto = mapper.toDTO(livro);
                        return ResponseEntity.ok(dto);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 se o ID não for um UUID válido
        }
    }


    @DeleteMapping("{id}")

    public ResponseEntity<Object> deletar(@PathVariable("id") String id) {
        return service.buscarPorId(UUID.fromString(id))
                .map(livro -> {
                    service.deletar(livro);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("")
    public ResponseEntity<List<ResultadoLivroDto>> pesquisa(
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "nome-autor", required = false) String nomeAutor,
            @RequestParam(value = "genero", required = false) GeneroLivro genero,
            @RequestParam(value = "ano-publicacao", required = false) Integer anoPublicacao,
            @RequestParam(value = "nome-editora", required = false) String nomeEditora,
            @RequestParam(value = "preco-minimo", required = false) Double precoMinimo,
            @RequestParam(value = "preco-maximo", required = false) Double precoMaximo
    ) {


        // Pesquisar livros com filtros
        List<Livro> livrosResultado = service.pesquisaporFiltro(isbn, genero, anoPublicacao, titulo, nomeAutor,nomeEditora,precoMinimo,precoMaximo);

        // Mapear a lista de livros para DTOs
        List<ResultadoLivroDto> resultado = livrosResultado.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }


    @PutMapping("{id}")
    public ResponseEntity<Object> atualizar(
            @PathVariable("id") String id, @RequestBody @Valid AtualizarLivroDto dto) {
        return service.buscarPorId(UUID.fromString(id))
                .map(livro -> {
                    // Atualiza apenas os campos que não são null
                    if (dto.getTitulo() != null) {
                        livro.setTitulo(dto.getTitulo());
                    }
                    if (dto.getIdAutor() != null) {
                        Autor autor = autorRepository.findById(dto.getIdAutor())
                                .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado"));
                        livro.setAutor(autor);
                    }
                    if (dto.getIsbn() != null) {
                        livro.setIsbn(dto.getIsbn());
                    }
                    if (dto.getPreco() != null) {
                        livro.setPreco(dto.getPreco());
                    }
                    if (dto.getGenero() != null) {
                        livro.setGenero(dto.getGenero());
                    }
                    if (dto.getDataPublicacao() != null) {
                        livro.setDataPublicacao(dto.getDataPublicacao());
                    }

                    service.atualizar(livro);

                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}