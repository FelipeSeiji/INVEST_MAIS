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
import com.repositorio.mvp.domain.auth.DTO.LoginRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.TokenResponseDTO;
import com.repositorio.mvp.domain.auth.DTO.Verify2FARequestDTO;
import com.repositorio.mvp.domain.auth.controller.LoginCommandController;
import com.repositorio.mvp.domain.auth.service.login.LoginService;
import com.repositorio.mvp.domain.auth.service.security.RateLimitingService;
import com.repositorio.mvp.shared.UserConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.bucket4j.Bucket;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {
    @InjectMocks
    private LoginCommandController loginController;

    @Mock
    private LoginService loginService;

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

        ResponseEntity<MessageResponseDTO> response = loginController.login(requestDTO, httpRequest);

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

        ResponseEntity<TokenResponseDTO> response = loginController.verify2FA(verifyRequest, httpRequest);

        assertNotNull(response.getBody());
        assertEquals(expectedJwt, response.getBody().token());
        
        verify(loginService).verify2FAAndGenerateToken(verifyRequest, MOCK_IP);
    }
}
