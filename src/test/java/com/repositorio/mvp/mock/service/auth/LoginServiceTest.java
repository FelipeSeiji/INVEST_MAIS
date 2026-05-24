package com.repositorio.mvp.mock.service.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import com.repositorio.mvp.common.security.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.auth.login.DTO.LoginRequestDTO;
import com.repositorio.mvp.domain.auth.login.DTO.Verify2FARequestDTO;
import com.repositorio.mvp.domain.auth.login.service.LoginAttemptService;
import com.repositorio.mvp.domain.auth.login.service.LoginService;
import com.repositorio.mvp.domain.auth.login.service.TwoFactorService;
import com.repositorio.mvp.domain.auth.token.service.TokenService;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.shared.UserConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private TwoFactorService twoFactorService;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private LoginAttemptService loginAttemptService;

    private User mockUser;
    private final String MOCK_IP = "192.168.1.10";

    @BeforeEach
    void setUp() {
        mockUser = UserConstants.createMockUser(); 
        mockUser.getSecurity().setEmailVerified(true); // E-mail verificado por padrão nos testes legados
    }

    @Test
    public void initiateLogin_WithValidCredentials_GeneratesAndSends2FA() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(UserConstants.USER.email(), UserConstants.USER.password());

        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(false);
        when(loginAttemptService.isBlocked(loginRequestDTO.email())).thenReturn(false);
        when(cryptoService.generateSha256Hash(loginRequestDTO.email())).thenReturn("hash");
        when(userRepository.findBySecurityEmailHash("hash")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequestDTO.password(), mockUser.getSecurity().getPassword())).thenReturn(true);

        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.getSecurity().generateTwoFactorCode("123456", LocalDateTime.now().plusMinutes(5));
            return null;
        }).when(twoFactorService).prepareAndSendTwoFactor(mockUser);

        ServiceResult<Void> result = loginService.initiateLogin(loginRequestDTO, MOCK_IP);
        assertTrue(result instanceof ServiceResult.Success);

        verify(twoFactorService).prepareAndSendTwoFactor(mockUser);
        
        assertNotNull(mockUser.getSecurity().getTwoFactorCode());
    }

    @Test
    public void initiateLogin_WithInvalidPassword_ReturnsErrorAndRegistersFailure() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(UserConstants.USER.email(), "invalidPassword");

        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(false);
        when(loginAttemptService.isBlocked(loginRequestDTO.email())).thenReturn(false);
        when(cryptoService.generateSha256Hash(loginRequestDTO.email())).thenReturn("hash");
        when(userRepository.findBySecurityEmailHash("hash")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequestDTO.password(), mockUser.getSecurity().getPassword())).thenReturn(false);

        ServiceResult<Void> result = loginService.initiateLogin(loginRequestDTO, MOCK_IP);
        assertTrue(result instanceof ServiceResult.Error);

        verify(loginAttemptService).loginFailed(MOCK_IP);
        verify(loginAttemptService).loginFailed(loginRequestDTO.email());

        verify(loginAttemptService).loginFailed(loginRequestDTO.email());
    }


    @Test
    public void initiateLogin_WhenIpIsBlocked_ReturnsError() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(UserConstants.USER.email(), UserConstants.USER.password());
        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(true);

        ServiceResult<Void> result = loginService.initiateLogin(loginRequestDTO, MOCK_IP);
        assertTrue(result instanceof ServiceResult.Error);

        verify(userRepository, never()).findBySecurityEmailHash(anyString());
    }

    @Test
    public void verify2FA_WithValidCode_ReturnsJwtToken() {
        String validCode = "123456";
        mockUser.getSecurity().generateTwoFactorCode(validCode, LocalDateTime.now().plusMinutes(5));
        
        Verify2FARequestDTO verify2faRequestDTO = new Verify2FARequestDTO(mockUser.getEmail(), validCode);
        String expectedJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mocked_token";

        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(false);
        when(cryptoService.generateSha256Hash(verify2faRequestDTO.email())).thenReturn("hash");
        when(userRepository.findBySecurityEmailHash("hash")).thenReturn(Optional.of(mockUser));
        when(tokenService.generateToken(mockUser.getId())).thenReturn(expectedJwt);

        ServiceResult<String> result = loginService.verify2FAAndGenerateToken(verify2faRequestDTO, MOCK_IP);
        assertTrue(result instanceof ServiceResult.Success);
        assertEquals(expectedJwt, ((ServiceResult.Success<String>) result).data());

        assertNull(mockUser.getSecurity().getTwoFactorCode());
        verify(loginAttemptService).loginSucceeded(MOCK_IP);
        verify(userRepository).save(mockUser);
    }
    @Test
    public void verify2FA_WithInvalidCode_ReturnsErrorAndRegistersFailure() {
        mockUser.getSecurity().generateTwoFactorCode("123456", LocalDateTime.now().plusMinutes(5));
        Verify2FARequestDTO request = new Verify2FARequestDTO(mockUser.getEmail(), "999999"); 
        
        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(false);
        when(cryptoService.generateSha256Hash(request.email())).thenReturn("hash");
        when(userRepository.findBySecurityEmailHash("hash")).thenReturn(Optional.of(mockUser));

        ServiceResult<String> result = loginService.verify2FAAndGenerateToken(request, MOCK_IP);
        assertTrue(result instanceof ServiceResult.Error);

        verify(loginAttemptService).loginFailed(MOCK_IP); 
        verify(loginAttemptService, never()).loginFailed(request.email()); // Confirmando mitigação de DoS (C-01)
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    public void verify2FA_WithExpiredCode_ReturnsError() {
        String validCode = "123456";

        mockUser.getSecurity().generateTwoFactorCode(validCode, LocalDateTime.now().minusMinutes(1)); 

        Verify2FARequestDTO request = new Verify2FARequestDTO(mockUser.getEmail(), validCode);
        
        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(false);
        when(cryptoService.generateSha256Hash(request.email())).thenReturn("hash");
        when(userRepository.findBySecurityEmailHash("hash")).thenReturn(Optional.of(mockUser));

        ServiceResult<String> result = loginService.verify2FAAndGenerateToken(request, MOCK_IP);
        assertTrue(result instanceof ServiceResult.Error);

        assertNull(mockUser.getSecurity().getTwoFactorCode()); 
        verify(userRepository).save(mockUser);
    }
}