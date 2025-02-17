package projetolivros.livros.Controller.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import projetolivros.livros.Dto.AtualizarLivroDto;
import projetolivros.livros.Dto.CadastroLivroDto;
import projetolivros.livros.Dto.LivroIDdto;
import projetolivros.livros.Dto.ResultadoLivroDto;
import projetolivros.livros.Model.Livro;
import projetolivros.livros.Repository.AutorRepository;
import projetolivros.livros.Repository.EditoraRepository;

@Mapper(componentModel = "spring", uses = AutorMapper.class )
public abstract class LivroMapper {

    @Autowired
    AutorRepository autorRepository;
    @Autowired
    EditoraRepository editoraRepository;


    @Mapping(target = "autor", expression = "java( autorRepository.findById(dto.idAutor()).orElse(null) )")
    @Mapping(target = "editora", expression = "java(dto.idEditora() != null ? editoraRepository.findById(dto.idEditora()).orElse(null) : null)")
    public abstract Livro toEntity(CadastroLivroDto dto);

    public abstract ResultadoLivroDto toDTO(Livro livro);

}