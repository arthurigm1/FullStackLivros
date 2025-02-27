package projetolivros.livros.Dto;

import projetolivros.livros.Model.Enum.UsuarioRole;

import java.util.UUID;

public record UsuarioDto(String email, UsuarioRole role, UUID id){
}
