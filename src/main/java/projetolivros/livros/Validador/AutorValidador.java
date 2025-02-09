package projetolivros.livros.Validador;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import projetolivros.livros.Exceptions.RegistroException;
import projetolivros.livros.Model.Autor;
import projetolivros.livros.Repository.AutorRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AutorValidador {


    @Autowired
    private AutorRepository autorRepository;


    public void validar(Autor autor) {
        if(existeAutor(autor)) {
            throw new RegistroException("Autor Ja cadastrado");
        }
    }

    private boolean existeAutor(Autor autor) {
        Optional<Autor> autorExistente = autorRepository.findByNomeAndDataNascimentoAndNacionalidade(
                autor.getNome(),
                autor.getDataNascimento(),
                autor.getNacionalidade());

        if (autorExistente.isPresent()) {
            // Se for uma criação de novo autor ou se o ID do autor encontrado for diferente do autor que está sendo editado
            return autor.getId() == null || !autorExistente.get().getId().equals(autor.getId());
        }
        return false;
    }

}
