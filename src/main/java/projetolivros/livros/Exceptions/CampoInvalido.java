package projetolivros.livros.Exceptions;

import lombok.Getter;

public class CampoInvalido extends RuntimeException
{
    @Getter
    String campo;

    public CampoInvalido(String campo ,String message) {
       super(message);
       this.campo = campo;

    }
}
