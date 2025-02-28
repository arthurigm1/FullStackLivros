package projetolivros.livros.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class EditoraAdminDto {

    private UUID id;
    private String nome;
    private String img;
    private int quantidadeLivros;
}
