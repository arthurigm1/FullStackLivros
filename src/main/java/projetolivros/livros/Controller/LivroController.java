package projetolivros.livros.Controller;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Controller.Mapper.LivroMapper;
import projetolivros.livros.Dto.CadastroLivroDto;
import projetolivros.livros.Dto.ResultadoLivroDto;
import projetolivros.livros.Model.GeneroLivro;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Service.LivroService;

import java.util.UUID;

@RestController
@RequestMapping("livros")
@RequiredArgsConstructor
public class LivroController  {

    private final LivroService livroService;
    private final LivroMapper livroMapper;

    @PostMapping
    public ResponseEntity<Object> cadastrarLivro(@RequestBody @Valid CadastroLivroDto cadastroLivroDto) {
        // mapear dto para entidade , enviar para o service validar e salvar
        // criar urlpara acesso dos dados
        // retornar codigo created com header location
        Livro livro = livroMapper.toEntity(cadastroLivroDto);
        livroService.salvar(livro);
        return ResponseEntity.ok().build();

    }

    @GetMapping("{id}")
    public ResponseEntity<ResultadoLivroDto> obterDetalher(@PathVariable("id") String id) {
        return livroService.buscarPorId(UUID.fromString(id)).map(livro -> {
            var dto = livroMapper.toDto(livro);
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletarLivro(@PathVariable("id") String id) {
        return livroService.buscarPorId(
                UUID.fromString(id))
                .map(livro -> {
            livroService.deletar(livro);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ResultadoLivroDto>> listarLivros(
            @RequestParam(value = "isbn", required = false)
            String isbn,
            @RequestParam(value = "genero", required = false)
            GeneroLivro generoLivro,
            @RequestParam(value = "nome-autor", required = false)
            String nomeAutor,
            @RequestParam(value = "ano-publicacao", required = false)
            Integer anoPublicacao,
            @RequestParam(value = "titulo", required = false)
            String titulo,
            @RequestParam(value ="pagina", defaultValue = "0")
            Integer pagina,
            @RequestParam(value = "tamanho-pagina" , defaultValue = "10")
            Integer tamanhoPagina
            )
    {
    Page<Livro> paginaResultado = livroService.pesquisaporFiltro(isbn,generoLivro,anoPublicacao,titulo,nomeAutor ,pagina,tamanhoPagina);
    Page<ResultadoLivroDto> resultado = paginaResultado.map(livroMapper::toDto);
    // var lista = resultado.stream().map(livroMapper::toDto).collect(Collectors.toList());
    return ResponseEntity.ok(resultado);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> atualizarLivro
            (@PathVariable("id") String id, @RequestBody  @Valid CadastroLivroDto cadastroLivroDto)
    {
        return livroService.buscarPorId(UUID.fromString(id)).map(livro -> {
           Livro entidade = livroMapper.toEntity(cadastroLivroDto);

           livro.setDataPublicacao(entidade.getDataPublicacao());
           livro.setIsbn(entidade.getIsbn());
           livro.setPreco(entidade.getPreco());
           livro.setGenero(entidade.getGenero());
           livro.setTitulo(entidade.getTitulo());
           livro.setAutor(entidade.getAutor());
            livroService.atualizar(livro);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
