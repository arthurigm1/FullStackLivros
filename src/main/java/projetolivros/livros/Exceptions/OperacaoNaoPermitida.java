package projetolivros.livros.Exceptions;

public class OperacaoNaoPermitida extends RuntimeException {
    public OperacaoNaoPermitida(String message) {
        super(message);
    }
}
