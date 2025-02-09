package projetolivros.livros.Controller.Mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import projetolivros.livros.Dto.AutorDto;
import projetolivros.livros.Model.Autor;

@Mapper(componentModel = "spring")
public interface AutorMapper {

    // Para propiedas que nao tem o mesmo nome usar @MAPPING SOUCE / TARGET
    @Mapping(source = "nome" , target = "nome")
    Autor toEntity(AutorDto autorDto);

    AutorDto toDto(Autor autor);
}
