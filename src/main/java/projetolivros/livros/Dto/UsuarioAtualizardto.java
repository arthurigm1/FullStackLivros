package projetolivros.livros.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UsuarioAtualizardto {
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String cpf; // Será tratado na lógica do serviço para não ser atualizado se já existir
}