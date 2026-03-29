package com.repositorio.mvp.domain.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.common.DTO.MessageResponseDTO;
import com.repositorio.mvp.domain.auth.service.token.TokenBlackListService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

/**
 * Controlador de operações administrativas do sistema.
 * Agrupa funções de suporte de infraestrutura e limpeza de dados.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class SystemAdminController {
    private final TokenBlackListService tokenBlackListService;

    /**
     * Rota de gatilho manual para forçar a limpeza imediata da tabela de tokens revogados.
     * Embora exista um cron job automático para isso, esta rota auxilia a manutenção por parte da equipe DevOps.
     * @return Mensagem de confirmação de execução da rotina.
     */
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
