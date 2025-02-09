package projetolivros.livros.Validador;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import projetolivros.livros.Exceptions.CampoInvalido;
import projetolivros.livros.Exceptions.RegistroException;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Repository.LivroRepository;

import java.util.Optional;

@Component
public class LivroValidador {

    private static final int ANO_EXIGENCIA = 2020;

    @Autowired
    private LivroRepository livroRepository;

    public void validar(Livro livro) {
        if(existeLivroComIsbn(livro)){
            throw new RegistroException("ISBN já cadastrado!");
        }
        if(isPrecoNulo(livro)){
            throw new CampoInvalido("preco","Preco Obrigatorio para livro acima de 2020.");
        }
    }

    private boolean isPrecoNulo(Livro livro) {
        return livro.getPreco() == null && livro.getDataPublicacao().getYear() >= ANO_EXIGENCIA;
    }

    private boolean existeLivroComIsbn(Livro livro) {
        Optional<Livro> livroEncontrado = livroRepository.findByIsbn(livro.getIsbn());

        if (livro.getId() == null) {
            return livroEncontrado.isPresent();
        }

        return livroEncontrado
                .map(Livro::getId)
                .stream()
                .anyMatch(id -> !id.equals(livro.getId()));
    }
}
