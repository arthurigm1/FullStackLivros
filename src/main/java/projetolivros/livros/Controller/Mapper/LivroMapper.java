package projetolivros.livros.Controller.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import projetolivros.livros.Dto.AtualizarLivroDto;
import projetolivros.livros.Dto.CadastroLivroDto;
import projetolivros.livros.Dto.ResultadoLivroDto;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Repository.AutorRepository;

@Mapper(componentModel = "spring", uses = AutorMapper.class )
public abstract class LivroMapper {

    @Autowired
    AutorRepository autorRepository;

    @Mapping(target = "genero", source = "genero")  // Adicionando explicitamente o mapeamento para genero
   @Mapping(target = "autor", expression = "java(autorRepository.findById(dto.idAutor()).orElse(null) )")
    public abstract Livro toEntity(CadastroLivroDto dto);
    @Mapping(target = "genero", source = "genero")  // Adicionando explicitamente o mapeamento para genero
    public abstract ResultadoLivroDto toDTO(Livro livro);

}
