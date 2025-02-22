package projetolivros.livros.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AvaliacaoLivroNota {
    private UUID id;
    private int nota;
    private String comentario;
    private String usuarioNome;
}
