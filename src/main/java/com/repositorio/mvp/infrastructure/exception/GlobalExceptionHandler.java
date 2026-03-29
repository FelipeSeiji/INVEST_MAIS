package com.repositorio.mvp.infrastructure.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.repositorio.mvp.infrastructure.exception.util.ProblemDetailBuilder;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ProblemDetailBuilder.buildValidation(ex);
    }

    //404
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
        return ProblemDetailBuilder.build(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
    }

    //409
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        return ProblemDetailBuilder.build(HttpStatus.CONFLICT, "Regra de negócio inválida", ex.getMessage());
    }

    //409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ProblemDetailBuilder.build(HttpStatus.CONFLICT, "Conflito de dados", "A operação não pôde ser concluída pois o registro já existe ou viola uma restrição única do sistema.");
    }

    //500
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("ERRO INTERNO SEVERO (500): Ocorreu uma exceção não tratada.", ex);
        
        return ProblemDetailBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", "Ocorreu um erro inesperado.");
    }
}