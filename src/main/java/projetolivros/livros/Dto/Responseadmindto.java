package projetolivros.livros.Dto;

import projetolivros.livros.Model.Enum.UsuarioRole;

import java.util.UUID;

public record Responseadmindto(String nome, String token, UUID id, String usuarioRole) {
}
