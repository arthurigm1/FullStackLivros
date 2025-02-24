package projetolivros.livros.Dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO ( String email,  String senha){}
