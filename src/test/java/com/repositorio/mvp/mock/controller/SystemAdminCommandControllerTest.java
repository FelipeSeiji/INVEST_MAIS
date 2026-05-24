package com.repositorio.mvp.mock.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.repositorio.mvp.common.DTO.MessageResponseDTO;
import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.admin.controller.SystemAdminCommandController;
import com.repositorio.mvp.domain.auth.token.service.TokenBlackListService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SystemAdminCommandControllerTest {
    @InjectMocks
    private SystemAdminCommandController systemAdminController;

    @Mock
    private TokenBlackListService tokenBlackListService;

    @Test
    public void forceRemoveExpiredTokens_CallsServiceAndReturnsSuccessMessage() {
        when(tokenBlackListService.removeExpiredTokens()).thenReturn(ServiceResult.success(null));

        ResponseEntity<MessageResponseDTO> responseEntity = systemAdminController.forceRemoveExpiredTokens();

        verify(tokenBlackListService).removeExpiredTokens();
        
        assertNotNull(responseEntity.getBody());
        assertEquals("Rotina de limpeza de tokens executada com sucesso.", responseEntity.getBody().message());
    }
}
