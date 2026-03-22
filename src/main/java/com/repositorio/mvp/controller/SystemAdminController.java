package com.repositorio.mvp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.DTO.common.MessageResponseDTO;
import com.repositorio.mvp.service.token.TokenBlackListService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class SystemAdminController {
    private final TokenBlackListService tokenBlackListService;

    @DeleteMapping("/tokens/expired")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')") 
    @Operation(summary = "Força a limpeza manual de tokens expirados", 
    description = "Apaga imediatamente do banco de dados todos os tokens JWT que já passaram da validade.")
    public MessageResponseDTO forceRemoveExpiredTokens() {
        tokenBlackListService.removeExpiredTokens();
        
        return new MessageResponseDTO("Rotina de limpeza de tokens executada com sucesso.");
    }
}
