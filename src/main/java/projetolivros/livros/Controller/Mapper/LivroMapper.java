package projetolivros.livros.Controller.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import projetolivros.livros.Dto.CadastroLivroDto;
import projetolivros.livros.Dto.ResultadoLivroDto;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Repository.AutorRepository;

@Mapper(componentModel = "spring", uses = AutorMapper.class )
public abstract class LivroMapper {

    @Autowired
    AutorRepository autorRepository;

    // Mapeia um CadastroLivroDto para a entidade Livro, preenchendo a relação com Autor.
    // O campo "autor" da entidade Livro será preenchido buscando um Autor no banco pelo ID informado no DTO.
    // Caso o ID do autor não seja encontrado, o valor será null.
    @Mapping(target  = "autor", expression = "java(autorRepository.findById(dto.id_usuario()).orElse(null))")
    public abstract Livro toEntity(CadastroLivroDto dto);


    public abstract ResultadoLivroDto toDto(Livro livro);
}
