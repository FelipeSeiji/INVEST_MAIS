package com.repositorio.mvp.infrastructure.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.repositorio.mvp.common.constants.LogMessageConstants;
import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.infrastructure.exception.util.ProblemDetailBuilder;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    //400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn(
            LogMessageConstants.ERROR.VALIDATION_FAILED, 
            ex.getErrorCount(), 
            ex.getMessage()
        );
        return ProblemDetailBuilder.buildValidation(ex);
    }

    //404
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn(LogMessageConstants.ERROR.RESOURCE_NOT_FOUND, 
            ex.getMessage()
        );
        return ProblemDetailBuilder.build(
            HttpStatus.NOT_FOUND, 
            MessageConstants.Exception.TITLE_NOT_FOUND, 
            ex.getMessage()
        );
    }

    //400
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn(
            LogMessageConstants.ERROR.BUSINESS_RULE_VIOLATION, 
            ex.getMessage()
        );
        return ProblemDetailBuilder.build(
            HttpStatus.BAD_REQUEST, 
            MessageConstants.Exception.TITLE_BAD_REQUEST, 
            ex.getMessage()
        );
    }

    //409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn(
            LogMessageConstants.ERROR.DATA_INTEGRITY_VIOLATION, 
            ex.getMostSpecificCause().getMessage());
        return ProblemDetailBuilder.build(
            HttpStatus.CONFLICT, 
            MessageConstants.Exception.TITLE_CONFLICT, 
            MessageConstants.Exception.DETAIL_DATA_INTEGRITY
        );
    }

    //500
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error(
            LogMessageConstants.ERROR.INTERNAL_SERVER_ERROR, 
            ex
        );
        
        return ProblemDetailBuilder.build(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            MessageConstants.Exception.TITLE_INTERNAL_ERROR, 
            MessageConstants.Exception.DETAIL_INTERNAL_ERROR
        );
    }
}