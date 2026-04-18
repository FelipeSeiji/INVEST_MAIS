package com.repositorio.mvp.common.result;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;

/**
 * Utilitário para mapear objetos ServiceResult para ResponseEntity do Spring.
 * Centraliza o tratamento dos estados da Sealed Interface para reduzir verbosidade nos Controllers.
 */
public class ResultMapper {

    /**
     * Converte um ServiceResult no ResponseEntity adequado.
     * 
     * @param <T> Tipo do dado de sucesso
     * @param result Resultado proveniente do serviço
     * @param successStatus Status HTTP a ser retornado em caso de sucesso (ex: OK, CREATED)
     * @return ResponseEntity configurado
     * @throws ErrorResponseException Se o resultado for NotFound ou Error
     */
    public static <T> ResponseEntity<T> toResponse(ServiceResult<T> result, HttpStatus successStatus) {
        return switch (result) {
            case ServiceResult.Success<T> s -> ResponseEntity.status(successStatus).body(s.data());
            
            case ServiceResult.NotFound<T> n -> throw new ErrorResponseException(
                HttpStatus.NOT_FOUND, 
                ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), 
                null
            );
            
            case ServiceResult.Error<T> e -> throw new ErrorResponseException(
                HttpStatus.BAD_REQUEST, 
                ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), 
                null
            );
        };
    }

    /**
     * Sobrecarga para retornos de sucesso com status 200 OK.
     */
    public static <T> ResponseEntity<T> ok(ServiceResult<T> result) {
        return toResponse(result, HttpStatus.OK);
    }

    /**
     * Sobrecarga para retornos de criação com status 201 Created.
     */
    public static <T> ResponseEntity<T> created(ServiceResult<T> result) {
        return toResponse(result, HttpStatus.CREATED);
    }

    /**
     * Sobrecarga para retornos sem corpo com status 204 No Content.
     */
    public static ResponseEntity<Void> noContent(ServiceResult<Void> result) {
        return toResponse(result, HttpStatus.NO_CONTENT);
    }
}
