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

import com.repositorio.mvp.DTO.auth.ForgotPasswordRequestDTO;
import com.repositorio.mvp.DTO.auth.LoginRequestDTO;
import com.repositorio.mvp.DTO.auth.ResetPasswordRequestDTO;
import com.repositorio.mvp.DTO.auth.TokenResponseDTO;
import com.repositorio.mvp.DTO.auth.Verify2FARequestDTO;
import com.repositorio.mvp.DTO.common.MessageResponseDTO;
import com.repositorio.mvp.controller.auth.AuthController;
import com.repositorio.mvp.service.auth.LoginService;
import com.repositorio.mvp.service.auth.PasswordRecoveryService;
import com.repositorio.mvp.service.auth.SessionService;
import com.repositorio.mvp.service.login.LoginAttemptService;
import com.repositorio.mvp.shared.UserConstants;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    private LoginService loginService;

    @Mock
    private SessionService sessionService;

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private HttpServletRequest httpRequest;

    private final String MOCK_IP = "192.168.0.50";


    @Test
    public void login_ExtractsIpAndCallsLoginService() {
        when(httpRequest.getRemoteAddr()).thenReturn(MOCK_IP);
        LoginRequestDTO requestDTO = new LoginRequestDTO(UserConstants.USER.email(), UserConstants.USER.password());
        MessageResponseDTO response = authController.login(requestDTO, httpRequest);

        assertNotNull(response);
        assertEquals("Código de verificação enviado para o seu e-mail.", response.message());
        
        verify(loginService).initiateLogin(requestDTO, MOCK_IP);
    }

    @Test
    public void verify2FA_ExtractsIpAndReturnsJwtToken() {
        when(httpRequest.getRemoteAddr()).thenReturn(MOCK_IP);
        Verify2FARequestDTO verifyRequest = new Verify2FARequestDTO(UserConstants.USER.email(), "123456");
        String expectedJwt = "meu.token.jwt";
        
        when(loginService.verify2FAAndGenerateToken(verifyRequest, MOCK_IP)).thenReturn(expectedJwt);

        TokenResponseDTO response = authController.verify2FA(verifyRequest, httpRequest);

        assertNotNull(response);
        assertEquals(expectedJwt, response.token());
        
        verify(loginService).verify2FAAndGenerateToken(verifyRequest, MOCK_IP);
    }

    @Test
    public void logout_CallsSessionService() {
        String token = "Bearer token_jwt_aqui";
        authController.logout(token);

        verify(sessionService).logout(token);
    }

    @Test
    public void forgotPassword_CallsPasswordRecoveryService() {
        when(httpRequest.getRemoteAddr()).thenReturn(MOCK_IP);
        ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO(UserConstants.USER.email());

        MessageResponseDTO response = authController.forgotPassword(requestDTO, httpRequest);

        assertNotNull(response);
        assertEquals("Se o e-mail existir, um link de recuperação foi enviado.", response.message());
        
        verify(passwordRecoveryService).createPasswordResetTokenForUser(requestDTO.email());
    }

    @Test
    public void resetPassword_CallsPasswordRecoveryService() {
        when(httpRequest.getRemoteAddr()).thenReturn(MOCK_IP);
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO("token_de_recuperacao", "NovaSenha@123");

        MessageResponseDTO response = authController.resetPassword(requestDTO, httpRequest);

        assertNotNull(response);
        assertEquals("Senha redefinida com sucesso.", response.message());
        
        verify(passwordRecoveryService).resetPassword(requestDTO.token(), requestDTO.newPassword());
    }
}