package projetolivros.livros.Controller.Mapper;

import org.mapstruct.Mapper;
import projetolivros.livros.Dto.RegisterRequestDTO;
import projetolivros.livros.Model.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(RegisterRequestDTO dto);
}
