package projetolivros.livros.Controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.AvaliacaoLivroDto;
import projetolivros.livros.Dto.AvaliacaoLivroNota;
import projetolivros.livros.Dto.MediaAvaliacaoDto;
import projetolivros.livros.Model.AvaliacaoLivro;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.AvaliacaoRepository;
import projetolivros.livros.Repository.LivroRepository;
import projetolivros.livros.Security.SecurityService;
import projetolivros.livros.Service.AvaliacaoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @GetMapping("/{livroId}")
    public ResponseEntity<List<AvaliacaoLivroNota>> getAvaliacoesPorLivro(@PathVariable UUID livroId) {

        List<AvaliacaoLivroNota> avaliacoes = avaliacaoService.getAvaliacoesPorLivro(livroId);
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/media/{livroId}")
    public ResponseEntity<MediaAvaliacaoDto> getMediaAvaliacoesPorLivro(@PathVariable UUID livroId) {
        MediaAvaliacaoDto media = avaliacaoService.calcularMediaNotasPorLivro(livroId);
        if (media == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(media);
    }
    @PostMapping("/{livroId}")
    public ResponseEntity<AvaliacaoLivro> criarAvaliacao(@PathVariable("livroId") UUID livroId, @RequestBody AvaliacaoLivroDto avaliacaoDTO) {

       avaliacaoService.criarAvaliacao(livroId,avaliacaoDTO);
        return ResponseEntity.ok().build();
    }





}
