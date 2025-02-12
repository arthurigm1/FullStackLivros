package projetolivros.livros.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projetolivros.livros.Dto.AutorDto;
import projetolivros.livros.Dto.LivroFavoritoDto;
import projetolivros.livros.Dto.ResultadoLivroDto;
import projetolivros.livros.Model.Favorito;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.FavoritoRepository;
import projetolivros.livros.Security.SecurityService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;
    @Autowired
    private SecurityService securityService;

    public Favorito favoritarLivro(Usuario usuario, Livro livro) {
        if (favoritoRepository.existsByUsuarioAndLivro(usuario, livro)) {
            throw new IllegalStateException("Este livro já foi favoritado!");
        }
        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setLivro(livro);
        return favoritoRepository.save(favorito); // Salva o favorito no banco de dados
    }


    public void desfavoritarLivro(Usuario usuario, Livro livro) {
       Favorito favorito = favoritoRepository.findByUsuarioIdAndLivroId(usuario.getId(), livro.getId());
        if (favorito != null) {
            favoritoRepository.delete(favorito);  // Remove o favorito
        }
    }

    public List<ResultadoLivroDto> listarLivrosFavoritos() {
        Usuario usuario = securityService.obterUsuarioLogado();
        // Busca os favoritos do usuário
        List<Favorito> favoritos = favoritoRepository.findByUsuarioId(usuario.getId());

        return favoritos.stream()
                .map(favorito -> new ResultadoLivroDto(
                        favorito.getLivro().getId(),
                        favorito.getLivro().getIsbn(),
                        favorito.getLivro().getTitulo(),
                        favorito.getLivro().getDataPublicacao(),
                        favorito.getLivro().getPreco(),
                        favorito.getLivro().getGenero(),
                        new AutorDto(
                                favorito.getLivro().getAutor().getId(),
                                favorito.getLivro().getAutor().getNome(),
                                favorito.getLivro().getAutor().getDataNascimento(),
                                favorito.getLivro().getAutor().getNacionalidade()
                        )
                ))
                .collect(Collectors.toList());
    }



}
