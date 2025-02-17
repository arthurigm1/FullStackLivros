package projetolivros.livros.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projetolivros.livros.Model.GeneroLivro;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.LivroRepository;
import projetolivros.livros.Repository.UsuarioRepository;
import projetolivros.livros.Security.SecurityService;
import projetolivros.livros.Security.TokenService;
import projetolivros.livros.Validador.LivroValidador;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static projetolivros.livros.Repository.Specs.LivroSpecs.*;
@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;
    private final LivroValidador livroValidador;
    private final SecurityService securityService;

    @Transactional
    public Livro salvar(Livro livro) {
        livroValidador.validar(livro);
        Usuario usuario = securityService.obterUsuarioLogado();
        livro.setUsuario(usuario);
        return livroRepository.save(livro);
    }

    @Transactional
    public Optional<Livro> buscarPorId(UUID id) {
        return livroRepository.findById(id);
    }
    public void deletar(Livro livro) {
        livroRepository.delete(livro);
    }

    public List<Livro> pesquisaporFiltro(
            String isbn,
            GeneroLivro generoLivro,
            Integer anoPublicacao,
            String titulo,
            String nomeAutor) {

        /*Specification<Livro> specs =
                Specification
                        .where(LivroSpecs.isbnEqual(isbn)
                                .and(LivroSpecs.generoEqual(generoLivro)
                                        .and(LivroSpecs.tituloLike(titulo))));*/

        Specification<Livro> specs = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        if (isbn != null) {
            //query = query and isbn =:isbn
            specs = specs.and(isbnEqual(isbn));
        }
        if (generoLivro != null) {
            specs = specs.and(generoEqual(generoLivro));
        }
        if (titulo != null) {
            specs = specs.and(tituloLike(titulo));
        }
        if(anoPublicacao != null) {
            specs = specs.and(anoPulicacaoAno(anoPublicacao));
        }
        if(nomeAutor != null) {
            specs = specs.and(nomeAutorLike(nomeAutor));
        }

        return  livroRepository.findAll(specs);
    }

    public void atualizar( Livro livro) {
        if(livro.getId() == null){
            throw new IllegalArgumentException("Livro nao esta salvo");
        }
        livroValidador.validar(livro);
        livroRepository.save(livro);
    }
}