package com.repositorio.mvp.domain.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.common.DTO.MessageResponseDTO;
import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.auth.service.token.TokenBlackListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Commands", description = "Endpoints de operações administrativas e infraestrutura")
public class SystemAdminCommandController {
    private final TokenBlackListService tokenBlackListService;

    @DeleteMapping("/tokens/expired")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')") 
    @Operation(summary = "Força a limpeza manual de tokens expirados", 
    description = "Apaga imediatamente do banco de dados todos os tokens JWT que já passaram da validade.")
    public MessageResponseDTO forceRemoveExpiredTokens() {
        tokenBlackListService.removeExpiredTokens();
        
        return new MessageResponseDTO(MessageConstants.Admin.TOKEN_CLEANUP_SUCCESS);
    }
}
