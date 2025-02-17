package projetolivros.livros.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projetolivros.livros.Dto.AutorDto;
import projetolivros.livros.Dto.LivroFavoritoDto;
import projetolivros.livros.Dto.ResultadoLivroDto;
import projetolivros.livros.Model.Autor;
import projetolivros.livros.Model.Favorito;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.FavoritoRepository;
import projetolivros.livros.Security.SecurityService;

import java.util.Collections;
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
        List<Favorito> favoritos = Optional.ofNullable(favoritoRepository.findByUsuarioId(usuario.getId()))
                .orElse(Collections.emptyList());

        // Se não houver favoritos, retorna lista vazia
        if (favoritos.isEmpty()) {
            return Collections.emptyList();
        }

        return favoritos.stream()
                .filter(favorito -> favorito.getLivro() != null) // Evita erros se o livro for nulo
                .map(favorito -> {
                    Livro livro = favorito.getLivro();
                    Autor autor = livro.getAutor(); // Pode ser null

                    return new ResultadoLivroDto(
                            livro.getId(),
                            livro.getIsbn(),
                            livro.getTitulo(),
                            livro.getDataPublicacao(),
                            livro.getPreco(),
                            livro.getGenero(),
                            livro.getDescricao(),
                            (autor != null) ? new AutorDto(
                                    autor.getId(),
                                    autor.getNome(),
                                    autor.getDataNascimento(),
                                    autor.getNacionalidade()
                            ) : null // Se autor for nulo, passa null
                    );
                })
                .collect(Collectors.toList());
    }




}
