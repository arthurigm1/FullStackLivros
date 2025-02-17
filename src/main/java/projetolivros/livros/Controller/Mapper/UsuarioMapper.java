package projetolivros.livros.Controller.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import projetolivros.livros.Dto.RegisterRequestDTO;
import projetolivros.livros.Dto.UsuarioAtualizardto;
import projetolivros.livros.Model.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "nome", target = "nome") // Se os nomes coincidirem
    @Mapping(source = "email", target = "email")
    @Mapping(source = "senha", target = "senha")
    Usuario toEntity(RegisterRequestDTO dto);

    UsuarioAtualizardto toDto(Usuario usuario);
}
