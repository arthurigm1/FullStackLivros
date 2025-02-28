package projetolivros.livros.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import projetolivros.livros.Dto.AutorAdminDto;
import projetolivros.livros.Exceptions.OperacaoNaoPermitida;
import projetolivros.livros.Model.Autor;
import projetolivros.livros.Model.Usuario;
import projetolivros.livros.Repository.AutorRepository;
import projetolivros.livros.Repository.LivroRepository;
import projetolivros.livros.Security.SecurityService;
import projetolivros.livros.Validador.AutorValidador;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutorService {

    private  final AutorRepository autorRepository;
    private final AutorValidador autorValidador;
    private final SecurityService securityService;
    private final LivroRepository livroRepository;



    public Autor save(Autor autor)
    {
        Usuario usuario = securityService.obterUsuarioLogado();
        autor.setIdusuario(usuario);
        autorValidador.validar(autor);
        return autorRepository.save(autor);
    }

    public List<AutorAdminDto> listarTodasAdmin() {
        List<Autor> autores = autorRepository.findAll();

        return autores.stream()
                .map(autor -> new AutorAdminDto(
                        autor.getId(),
                        autor.getNome(),
                        autor.getDataNascimento(),
                        autor.getNacionalidade(),
                        autor.getDescricao(),
                        autor.getImg(),
                        autor.getLivros().size()
                ))
                .collect(Collectors.toList());
    }


    public Optional<Autor> findById(UUID id) {
        return autorRepository.findById(id);
    }
    public void delete(Autor autor) {
        if (possulivro(autor)) {
            throw new OperacaoNaoPermitida("Autor possui livros cadastrados"); // TRATAR ERRO DE AUTOR COM LIVRO CADASTRADO
        }
        autorRepository.delete(autor);
    }

    public List<Autor> pesquisa(String nome, String Nacionalidade) {
    if(nome != null && Nacionalidade != null){
        return autorRepository.findByNomeAndNacionalidade(nome,Nacionalidade);
    }
    if(nome != null){
        return autorRepository.findByNome(nome);
    }
    if (Nacionalidade != null){
        return autorRepository.findByNacionalidade(Nacionalidade);
    }
    return autorRepository.findAll();
    }
    public List<Autor> pesquisaByExample(String nome, String Nacionalidade) {
       var autor = new Autor();
       autor.setNome(nome);
       autor.setNacionalidade(Nacionalidade);
        ExampleMatcher matcher = ExampleMatcher.
                matching().
                withIgnoreNullValues().
                withIgnoreCase().
                withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
       Example<Autor> autorExample = Example.of(autor, matcher);
       return autorRepository.findAll(autorExample);
    }

    public void atualizar(Autor autor) {
        autorRepository.save(autor);
    }

    public boolean possulivro(Autor autor) {
    return  livroRepository.existsByAutor(autor);
    }


}
