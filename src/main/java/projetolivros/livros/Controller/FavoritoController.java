package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.FavoritoRequestDto;
import projetolivros.livros.Dto.ResultadoLivroDto;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.LivroRepository;
import projetolivros.livros.Security.SecurityService;
import projetolivros.livros.Service.FavoritoService;

import java.util.List;

@Tag(name = "Favoritos", description = "Endpoints para gerenciamento de livros favoritos")
@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final SecurityService securityService;
    private final LivroRepository livroRepository;



    @Operation(summary = "Adiciona um livro aos favoritos do usuário")
    @ApiResponse(responseCode = "200", description = "Livro favoritado com sucesso")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @PostMapping
    public ResponseEntity<Void> favoritarLivro(@RequestBody FavoritoRequestDto favoritoRequest) {
        Usuario usuario = securityService.obterUsuarioLogado();
        Livro livro = livroRepository.findById(favoritoRequest.getLivro())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        favoritoService.favoritarLivro(usuario, livro);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove um livro dos favoritos do usuário")
    @ApiResponse(responseCode = "200", description = "Livro removido dos favoritos com sucesso")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @PostMapping("/desfavoritar")
    public ResponseEntity<Void> desfavoritarLivro(@RequestBody FavoritoRequestDto favoritoRequest) {
        Usuario usuario = securityService.obterUsuarioLogado();
        Livro livro = livroRepository.findById(favoritoRequest.getLivro())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        favoritoService.desfavoritarLivro(usuario, livro);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lista os livros favoritados do usuário")
    @ApiResponse(responseCode = "200", description = "Lista de livros favoritos retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ResultadoLivroDto>> obterLivrosFavoritos() {
        return ResponseEntity.ok(favoritoService.listarLivrosFavoritos());
    }
}
