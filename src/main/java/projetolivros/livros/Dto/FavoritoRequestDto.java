package projetolivros.livros.Dto;

import lombok.Getter;
import lombok.Setter;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Model.Usuario;

import java.util.UUID;

@Getter
@Setter
public class FavoritoRequestDto {

    private UUID livro;


}
