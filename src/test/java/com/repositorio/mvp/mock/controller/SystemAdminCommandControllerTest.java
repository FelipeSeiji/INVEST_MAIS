package com.repositorio.mvp.mock.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.common.DTO.MessageResponseDTO;
import com.repositorio.mvp.domain.admin.controller.SystemAdminCommandController;
import com.repositorio.mvp.domain.auth.service.token.TokenBlackListService;

@ExtendWith(MockitoExtension.class)
public class SystemAdminCommandControllerTest {
    @InjectMocks
    private SystemAdminCommandController systemAdminController;

    @Mock
    private TokenBlackListService tokenBlackListService;

    @Test
    public void forceRemoveExpiredTokens_CallsServiceAndReturnsSuccessMessage() {
        MessageResponseDTO response = systemAdminController.forceRemoveExpiredTokens();

        verify(tokenBlackListService).removeExpiredTokens();
        
        assertNotNull(response);
        assertEquals("Rotina de limpeza de tokens executada com sucesso.", response.message());
    }
}
