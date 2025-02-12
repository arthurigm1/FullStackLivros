package projetolivros.livros.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projetolivros.livros.Dto.FavoritoRequestDto;
import projetolivros.livros.Dto.LivroFavoritoDto;
import projetolivros.livros.Dto.ResultadoLivroDto;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.LivroRepository;
import projetolivros.livros.Security.SecurityService;
import projetolivros.livros.Service.FavoritoService;

import java.util.List;

@RestController
@RequestMapping("/favoritos")
public class FavoritoController {
    @Autowired
    private FavoritoService favoritoService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private LivroRepository livroRepository;

    @PostMapping("")
    public ResponseEntity<Void> favoritarLivro(@RequestBody FavoritoRequestDto favoritoRequest) {
        // Recupera o usuário logado através do serviço de segurança
        Usuario usuario = securityService.obterUsuarioLogado(); // Esse método pega o usuário autenticado

        // Encontra o livro pelo ID
        Livro livro = livroRepository.findById(favoritoRequest.getLivro())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        // Chama o serviço para favoritar o livro
        favoritoService.favoritarLivro(usuario, livro);

        // Retorna a resposta de sucesso
        return ResponseEntity.ok().build();
    }


    @PostMapping("/desfavoritar")
    public ResponseEntity<Void> desfavoritarLivro(@RequestBody FavoritoRequestDto favoritoRequest) {
        Usuario usuarioId = securityService.obterUsuarioLogado();
        Livro livro = livroRepository.findById(favoritoRequest.getLivro())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        favoritoService.desfavoritarLivro(usuarioId, livro);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<List<ResultadoLivroDto>> obterLivrosFavoritos() {

        return ResponseEntity.ok(favoritoService.listarLivrosFavoritos());
    }
}
