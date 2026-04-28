package com.repositorio.mvp.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.auth.service.auth.SessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação - Sessão", description = "Gerenciamento de sessão (Logout)")
public class SessionCommandController {
    private final SessionService sessionService;

    @PostMapping("/logout")
    @Operation(summary = "Realiza o logout do usuário", description = "Invalida o token JWT, encerrando a sessão.")
    @ApiResponse(responseCode = "204", description = "Logout realizado")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") @NonNull String token) {
        SecurityContextHolder.clearContext();
        ServiceResult<Void> result = sessionService.logout(token);
        
        return switch (result) {
            case ServiceResult.Success<Void> _ -> ResponseEntity.noContent().build();
            case ServiceResult.NotFound<Void> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<Void> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }
}
