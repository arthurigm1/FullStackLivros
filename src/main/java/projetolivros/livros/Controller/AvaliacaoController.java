package projetolivros.livros.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.AvaliacaoLivroDto;
import projetolivros.livros.Dto.AvaliacaoLivroNota;
import projetolivros.livros.Dto.MediaAvaliacaoDto;
import projetolivros.livros.Model.AvaliacaoLivro;
import projetolivros.livros.Service.AvaliacaoService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Avaliações", description = "Endpoints para gerenciamento de avaliações de livros")
@RestController
@AllArgsConstructor
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @Operation(summary = "Obtém as avaliações de um livro pelo ID")
    @ApiResponse(responseCode = "200", description = "Lista de avaliações retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @GetMapping("/{livroId}")
    public ResponseEntity<List<AvaliacaoLivroNota>> getAvaliacoesPorLivro(@PathVariable UUID livroId) {
        List<AvaliacaoLivroNota> avaliacoes = avaliacaoService.getAvaliacoesPorLivro(livroId);
        return ResponseEntity.ok(avaliacoes);
    }

    @Operation(summary = "Obtém a média das avaliações de um livro pelo ID")
    @ApiResponse(responseCode = "200", description = "Média das avaliações retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    @GetMapping("/media/{livroId}")
    public ResponseEntity<MediaAvaliacaoDto> getMediaAvaliacoesPorLivro(@PathVariable UUID livroId) {
        MediaAvaliacaoDto media = avaliacaoService.calcularMediaNotasPorLivro(livroId);
        if (media == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(media);
    }

    @Operation(summary = "Cria uma avaliação para um livro")
    @ApiResponse(responseCode = "200", description = "Avaliação criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro na requisição")
    @PostMapping("/{livroId}")
    public ResponseEntity<Void> criarAvaliacao(@PathVariable("livroId") UUID livroId, @RequestBody @Valid AvaliacaoLivroDto avaliacaoDTO) {
        avaliacaoService.criarAvaliacao(livroId, avaliacaoDTO);
        return ResponseEntity.ok().build();
    }
}
