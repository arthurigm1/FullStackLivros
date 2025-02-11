package projetolivros.livros.Dto;

import java.util.UUID;

public record ResponseDTO (String name, String token, UUID id) { }
