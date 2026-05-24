package com.repositorio.mvp.mock.controller.auth;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.repositorio.mvp.common.DTO.MessageResponseDTO;
import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.auth.password.DTO.ForgotPasswordRequestDTO;
import com.repositorio.mvp.domain.auth.password.DTO.ResetPasswordRequestDTO;
import com.repositorio.mvp.domain.auth.password.controller.PasswordCommandController;
import com.repositorio.mvp.domain.auth.password.service.PasswordRecoveryService;
import com.repositorio.mvp.shared.UserConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordControllerTest {
    @InjectMocks
    private PasswordCommandController passwordController;

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @Mock
    private HttpServletRequest httpRequest;

    private final String MOCK_IP = "192.168.0.50";

    @Test
    public void forgotPassword_CallsPasswordRecoveryService() {
        when(httpRequest.getRemoteAddr()).thenReturn(MOCK_IP);
        ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO(UserConstants.USER.email());
        when(passwordRecoveryService.initiatePasswordRecovery(requestDTO.email(), MOCK_IP)).thenReturn(ServiceResult.success(null));

        ResponseEntity<MessageResponseDTO> response = passwordController.forgotPassword(requestDTO, httpRequest);

        assertNotNull(response.getBody());
        assertEquals("Se o e-mail existir, um link de recuperação foi enviado.", response.getBody().message());
        
        verify(passwordRecoveryService).initiatePasswordRecovery(requestDTO.email(), MOCK_IP);
    }

    @Test
    public void resetPassword_CallsPasswordRecoveryService() {
        when(httpRequest.getRemoteAddr()).thenReturn(MOCK_IP);
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO("token_de_recuperacao", "NovaSenha@123");
        when(passwordRecoveryService.resetPassword(requestDTO.token(), requestDTO.newPassword())).thenReturn(ServiceResult.success(null));

        ResponseEntity<MessageResponseDTO> response = passwordController.resetPassword(requestDTO, httpRequest);

        assertNotNull(response.getBody());
        assertEquals("Senha redefinida com sucesso.", response.getBody().message());
        
        verify(passwordRecoveryService).resetPassword(requestDTO.token(), requestDTO.newPassword());
    }
}
