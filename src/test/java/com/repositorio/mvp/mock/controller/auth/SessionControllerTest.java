package com.repositorio.mvp.mock.controller.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.auth.controller.SessionCommandController;
import com.repositorio.mvp.domain.auth.service.auth.LogoutService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {
    @InjectMocks
    private SessionCommandController sessionController;

    @Mock
    private LogoutService sessionService;

    @Test
    public void logout_CallsSessionService() {
        String token = "Bearer token_jwt_aqui";
        when(sessionService.logout(token)).thenReturn(ServiceResult.success(null));
        
        ResponseEntity<Void> response = sessionController.logout(token);

        assertEquals(204, response.getStatusCode().value());
        verify(sessionService).logout(token);
    }
}
