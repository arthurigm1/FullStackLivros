package projetolivros.livros.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handle(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

       List<ErroCampo> listaerros = fieldErrors.stream().map(fieldError -> new ErroCampo(
                fieldError.getField(),
                fieldError.getDefaultMessage())).
                collect(Collectors.toList());

        return new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de validacao",
                listaerros);
    }

    @ExceptionHandler(RegistroException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResposta handleRegistroDuplicado(RegistroException ex) {
       return ErroResposta.respostaConflito(ex.getMessage());

    }

    @ExceptionHandler(OperacaoNaoPermitida.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResposta handleOperacaoNaoPermitida(OperacaoNaoPermitida e){
        return ErroResposta.respostaPadrao(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErroResposta handleErrosNaoTratados(RuntimeException e){
        return new ErroResposta(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Ocorreu um erro interno.",List.of());
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErroResposta handleAcessDenied(AccessDeniedException e){
        return new ErroResposta(HttpStatus.FORBIDDEN.value(),"Acesso negado",List.of());
    }
    @ExceptionHandler(CampoInvalido.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handleCampoInvalido(CampoInvalido campoInvalido){
    return new ErroResposta(
            HttpStatus.UNPROCESSABLE_ENTITY.value()
            ,"Erro de validacao",List.of(new ErroCampo(campoInvalido.getCampo(),campoInvalido.getMessage())));
    }
}
