package com.repositorio.mvp.mock.controller.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.common.DTO.MessageResponseDTO;
import com.repositorio.mvp.domain.auth.DTO.ForgotPasswordRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.LoginRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.ResetPasswordRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.TokenResponseDTO;
import com.repositorio.mvp.domain.auth.DTO.Verify2FARequestDTO;
import com.repositorio.mvp.domain.auth.controller.AuthCommandController;
import com.repositorio.mvp.domain.auth.service.auth.PasswordRecoveryService;
import com.repositorio.mvp.domain.auth.service.auth.SessionService;
import com.repositorio.mvp.domain.auth.service.login.LoginAttemptService;
import com.repositorio.mvp.domain.auth.service.login.LoginService;
import com.repositorio.mvp.shared.UserConstants;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.auth.service.security.RateLimitingService;
import io.github.bucket4j.Bucket;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @InjectMocks
    private AuthCommandController authController;

    @Mock
    private LoginService loginService;

    @Mock
    private SessionService sessionService;

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private RateLimitingService rateLimitingService;

    @Mock
    private Bucket bucket;

    @Mock
    private HttpServletRequest httpRequest;

    private final String MOCK_IP = "192.168.0.50";


    @Test
    public void login_ExtractsIpAndCallsLoginService() {
        when(httpRequest.getRemoteAddr()).thenReturn(MOCK_IP);
        when(rateLimitingService.resolveLoginBucket(MOCK_IP)).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);
        
        LoginRequestDTO requestDTO = new LoginRequestDTO(UserConstants.USER.email(), UserConstants.USER.password());
        when(loginService.initiateLogin(requestDTO, MOCK_IP)).thenReturn(ServiceResult.success(null));

        ResponseEntity<MessageResponseDTO> response = authController.login(requestDTO, httpRequest);

        assertNotNull(response.getBody());
        assertEquals("Código de verificação enviado para o seu e-mail.", response.getBody().message());
        
        verify(loginService).initiateLogin(requestDTO, MOCK_IP);
    }

    @Test
    public void verify2FA_ExtractsIpAndReturnsJwtToken() {
        when(httpRequest.getRemoteAddr()).thenReturn(MOCK_IP);
        Verify2FARequestDTO verifyRequest = new Verify2FARequestDTO(UserConstants.USER.email(), "123456");
        String expectedJwt = "meu.token.jwt";
        
        when(loginService.verify2FAAndGenerateToken(verifyRequest, MOCK_IP)).thenReturn(ServiceResult.success(expectedJwt));

        ResponseEntity<TokenResponseDTO> response = authController.verify2FA(verifyRequest, httpRequest);

        assertNotNull(response.getBody());
        assertEquals(expectedJwt, response.getBody().token());
        
        verify(loginService).verify2FAAndGenerateToken(verifyRequest, MOCK_IP);
    }

    @Test
    public void logout_CallsSessionService() {
        String token = "Bearer token_jwt_aqui";
        when(sessionService.logout(token)).thenReturn(ServiceResult.success(null));
        
        ResponseEntity<Void> response = authController.logout(token);

        assertEquals(204, response.getStatusCode().value());
        verify(sessionService).logout(token);
    }

    @Test
    public void forgotPassword_CallsPasswordRecoveryService() {
        when(httpRequest.getRemoteAddr()).thenReturn(MOCK_IP);
        ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO(UserConstants.USER.email());
        when(passwordRecoveryService.initiatePasswordRecovery(requestDTO.email(), MOCK_IP)).thenReturn(ServiceResult.success(null));

        ResponseEntity<MessageResponseDTO> response = authController.forgotPassword(requestDTO, httpRequest);

        assertNotNull(response.getBody());
        assertEquals("Se o e-mail existir, um link de recuperação foi enviado.", response.getBody().message());
        
        verify(passwordRecoveryService).initiatePasswordRecovery(requestDTO.email(), MOCK_IP);
    }

    @Test
    public void resetPassword_CallsPasswordRecoveryService() {
        when(httpRequest.getRemoteAddr()).thenReturn(MOCK_IP);
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO("token_de_recuperacao", "NovaSenha@123");
        when(passwordRecoveryService.resetPassword(requestDTO.token(), requestDTO.newPassword())).thenReturn(ServiceResult.success(null));

        ResponseEntity<MessageResponseDTO> response = authController.resetPassword(requestDTO, httpRequest);

        assertNotNull(response.getBody());
        assertEquals("Senha redefinida com sucesso.", response.getBody().message());
        
        verify(passwordRecoveryService).resetPassword(requestDTO.token(), requestDTO.newPassword());
    }
}