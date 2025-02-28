package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Controller.Mapper.LivroMapper;
import projetolivros.livros.Dto.AtualizarLivroDto;
import projetolivros.livros.Dto.CadastroLivroDto;
import projetolivros.livros.Dto.ResultadoLivroDto;
import projetolivros.livros.Model.Autor;
import projetolivros.livros.Model.Editora;
import projetolivros.livros.Model.Enum.GeneroLivro;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Repository.AutorRepository;
import projetolivros.livros.Repository.EditoraRepository;
import projetolivros.livros.Service.LivroService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Livros", description = "Gerenciamento de livros na biblioteca")
@RestController
@RequestMapping("livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService service;
    private final LivroMapper mapper;
    private final AutorRepository autorRepository;
    private final EditoraRepository editoraRepository;

    @Operation(summary = "Cadastra um novo livro")
    @ApiResponse(responseCode = "200", description = "Livro cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para cadastro do livro")
    @PostMapping
    @Transactional
    public ResponseEntity<Object> cadastrarLivro(@RequestBody @Valid CadastroLivroDto cadastroLivroDto) {
        Livro livro = mapper.toEntity(cadastroLivroDto);
        service.salvar(livro);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obtém detalhes de um livro por ID")
    @ApiResponse(responseCode = "200", description = "Detalhes do livro retornados com sucesso")
    @ApiResponse(responseCode = "400", description = "ID inválido")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @GetMapping("{id}")

    public ResponseEntity<ResultadoLivroDto> obterDetalhes(@PathVariable("id") String id) {
        try {
            UUID livroId = UUID.fromString(id);
            return service.buscarPorId(livroId)
                    .map(livro -> ResponseEntity.ok(mapper.toDTO(livro)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Deleta um livro por ID")
    @ApiResponse(responseCode = "204", description = "Livro deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") String id) {
        return service.buscarPorId(UUID.fromString(id))
                .map(livro -> {
                    service.deletar(livro);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Pesquisa livros por diversos filtros")
    @ApiResponse(responseCode = "200", description = "Lista de livros retornada com sucesso")
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
        List<Livro> livrosResultado = service.pesquisaporFiltro(isbn, genero, anoPublicacao, titulo, nomeAutor, nomeEditora, precoMinimo, precoMaximo);
        List<ResultadoLivroDto> resultado = livrosResultado.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Atualiza os dados de um livro")
    @ApiResponse(responseCode = "204", description = "Livro atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @PutMapping("{id}")
    public ResponseEntity<Object> atualizar(
            @Parameter(description = "ID do livro") @PathVariable("id") String id,
            @RequestBody @Valid AtualizarLivroDto dto) {
        return service.buscarPorId(UUID.fromString(id))
                .map(livro -> {
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
                    if (dto.getIdEditora() != null) {
                        Editora editora = editoraRepository.findById(dto.getIdEditora())
                                .orElseThrow(() -> new IllegalArgumentException("Editora não encontrada"));
                        livro.setEditora(editora);
                    }
                    if (dto.getDescricao() != null) {
                        livro.setDescricao(dto.getDescricao());
                    }
                    if (dto.getImg() != null) {
                        livro.setImg(dto.getImg());
                    }
                    if (dto.getEstoque() != 0) {
                        livro.setEstoque(dto.getEstoque());
                    }

                    service.atualizar(livro);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
