package projetolivros.livros.Dto;

import java.util.UUID;

public record ResponseDTO (String nome, String token, UUID id) { }
