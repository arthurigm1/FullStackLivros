package projetolivros.livros.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record AutorAdminDto(
        UUID id,
                            String nome,

                            LocalDate dataNascimento,


                            String nacionalidade,

                            String descricao,

                            String img,
                             int quantidadeLivros) {

}
