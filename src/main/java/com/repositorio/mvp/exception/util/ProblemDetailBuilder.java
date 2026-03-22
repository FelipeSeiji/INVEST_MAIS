package com.repositorio.mvp.exception.util;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ProblemDetailBuilder {
    public static ProblemDetail build(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create("about:blank"));
        return problemDetail;
    }

    public static ProblemDetail buildValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = build(HttpStatus.BAD_REQUEST, "Erro de validação", "Um ou mais campos estão inválidos");
        
        List<String> errors = ex.getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList();
                
        problemDetail.setProperty("erros", errors);
        return problemDetail;
    }
}