package projetolivros.livros.Controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Model.AvaliacaoLivro;
import projetolivros.livros.Repository.AvaliacaoRepository;
import projetolivros.livros.Repository.LivroRepository;
import projetolivros.livros.Service.AvaliacaoService;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;



    @GetMapping("/{livroId}")
    public ResponseEntity<List<AvaliacaoLivro>> getAvaliacoesPorLivro(@PathVariable UUID livroId) {
        List<AvaliacaoLivro> avaliacoes = avaliacaoService.getAvaliacoesPorLivro(livroId);
        return ResponseEntity.ok(avaliacoes);
    }

    @PostMapping("/{livroId}")
    public ResponseEntity<?> criarAvaliacao(@PathVariable UUID livroId, @RequestBody AvaliacaoLivro avaliacao) {
        return avaliacaoService.criarAvaliacao(livroId, avaliacao)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/{livroId}/media")
    public ResponseEntity<Double> getMediaAvaliacoes(@PathVariable UUID livroId) {
        double media = avaliacaoService.getMediaAvaliacoes(livroId);
        return ResponseEntity.ok(media);
    }
}
