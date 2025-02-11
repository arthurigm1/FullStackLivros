package projetolivros.livros.Controller.Mapper;

import org.mapstruct.Mapper;
import projetolivros.livros.Dto.LivroCarrinhoRequestdto;
import projetolivros.livros.Dto.RegisterRequestDTO;
import projetolivros.livros.Model.Carrinho;
import projetolivros.livros.Model.Usuario;

@Mapper(componentModel = "spring")
public interface CarrinhoMapper {
    Carrinho toEntity(LivroCarrinhoRequestdto livroCarrinhoRequestdto);
}
